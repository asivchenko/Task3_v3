package org.example;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;


// Утилита кэширования

public class Utils {    //https://javarush.com/groups/posts/2281-dinamicheskie-proksi
    // для обеспечения безопасности доступа из нескольких потоков переходим на ConcurrentHashMa

      private static ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
      private static  CacheInvocationHandler<?>  cacheInvocationHandler; // храним сылку чтобв закрывать  из любого места
        // создаем планировщик запуска заданий для выполнения задач по расписанию
        //с одним потоком будет использоть для выполнения задач по очистке устаревших значений кэша

    public static<T> T cache (T obj){
         cacheInvocationHandler = new CacheInvocationHandler<>(obj);
        return (T) Proxy.newProxyInstance(
                // новый динамический проккси объект для заданного класса ,
                //интерфейсов и обработчика вызовов
                obj.getClass().getClassLoader(), //загрузчик классов для создания прокси
                obj.getClass().getInterfaces(),  //интерфейсы которые реализует объект
                cacheInvocationHandler
                //new CacheInvocationHandler <> (obj)   // обработчик вызовов для прокси
        );
    }
    public static void shutdown () {  //будем закрывать где хотим
        if (cacheInvocationHandler !=null)
            cacheInvocationHandler.shutDown();
    }

    //Вариант 2  реализация перехватчик вызовов к  объекту
    private static class CacheInvocationHandler<T> implements InvocationHandler {
        private   final Map <Long, Map <Method,CacheValue>> cache; //= new ConcurrentHashMap<>();
        private  long  HasCodeMutator =0;
        private  int FirstMutator=1;
        private long currentHasCode=0;

        private final T obj;
        private final ScheduledFuture<T> cleanerTask;
        private final long cleanerintervalMillins=1000;
        private final ReentrantLock lock= new ReentrantLock();

        public CacheInvocationHandler(T  obj)
        {
            this.obj = obj;
            this.cache=new ConcurrentHashMap<>();
            this.cleanerTask = (ScheduledFuture<T>) executor.scheduleAtFixedRate(
                    this::cleanCache,
                    cleanerintervalMillins,
                    cleanerintervalMillins,
                    TimeUnit.MILLISECONDS);

        }
        private void cleanCache() {
            lock.lock();
            try {
                cache.forEach((key, value) -> {
                    value.entrySet().removeIf(entry -> entry.getValue().getTimeLife() > System.currentTimeMillis());
                    if (value.isEmpty())
                        cache.remove(key);  // если все удалили
                });
            } finally {
                lock.unlock();
            }
        }


        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            //определяем  метод через класс проксируемого объектаж
            //currentsnapshot=findscurrentsnapshot();
            Method objmethod = obj.getClass().getMethod(method.getName(), method.getParameterTypes());
            if (objmethod.isAnnotationPresent(Mutator.class)) {
                if (FirstMutator==1) {
                    HasCodeMutator = 0;
                    FirstMutator=0;
                }
                long a= this.HasCodeMutator+generatehascode(method,args);
                HasCodeMutator=HasCodeMutator +generatehascode(method,args);
                System.out.println("HasCodeMutator"+ HasCodeMutator);
                return method.invoke(obj, args);
            }
            else if (objmethod.isAnnotationPresent(Cache.class)) {
                return  handlerCache(objmethod, args);
            }
            return method.invoke(obj, args)  ;
        }

        private Object  handlerCache(Method method, Object[] args) throws  Throwable {
            lock.lock();
            try {
                currentHasCode = HasCodeMutator;
                FirstMutator = 1;
                Map<Method, CacheValue> currentcache = cache.get(currentHasCode);
                if (currentcache != null && !currentcache.isEmpty()) {  //мапа не пустая
                    CacheValue cachevalue = currentcache.get(method);   //ищем по ключу
                    if (cachevalue == null || cachevalue.getTimeLife() > method.getAnnotation(Cache.class).value()) {
                        Object result = method.invoke(obj, args);
                        currentcache.put(method, new CacheValue(result, System.currentTimeMillis()));
                        return result;
                    } else {
                        // нашли кэш
                        cachevalue.setTimeLife(System.currentTimeMillis()); // обновляем время жизни
                        //System.out.println("возвращаем кэш ="+cachevalue.getValue());
                        return cachevalue.getValue();
                    }
                } else {
                    Object result = method.invoke(obj, args);
                    currentcache = new HashMap<>();
                    currentcache.put(method, new CacheValue(result, System.currentTimeMillis()));
                    cache.put(currentHasCode, currentcache);
                    return result;
                }
            }
            finally {

                lock.unlock();
            }

        }

        private int generatehascode (Method methodmutator, Object[] args)
        {
            int result = Objects.hash(methodmutator);
            result = 31 * result + Arrays.hashCode(args);
            return result;
        }

        static class CacheValue {

            private final Object value;
            private  long timeLife; // время жизни
            public CacheValue (Object value, long timeLife)
            {
                this.value =value;
                this.timeLife =timeLife;
            }

            public Object getValue() {
                return value;
            }
            public void  setTimeLife (long timeLife) {
                this.timeLife=timeLife;
            }
            public long getTimeLife () {
                return System.currentTimeMillis() - this.timeLife;
            }

            @Override
            public String toString ()
            {
                return "cacheValue (" + "value=" +value + " Время жизни(разность между системой и начальным ) =" + getTimeLife() + ')';
            }
        }

       // @Override   не понял почему не отрабатывает toString
        public String pech_cache () {
            int countshop=0;
            StringBuilder sb = new StringBuilder();
            sb.append( "cacheMap: \n=");
            for (Map.Entry<Long,Map< Method, CacheValue>> entry : cache.entrySet())
            {
                countshop=countshop+1;
                Long HasCode =entry.getKey();
                Map<Method, CacheValue> methodcache =entry.getValue();

                sb.append("\n Снимок " + countshop + ": { ");
                sb.append(" hascode состояния ").append(HasCode);
                sb.append(" Список МАП КЭШ: { \n");
                for (Map.Entry<Method, CacheValue> entrycache : methodcache.entrySet())
                {
                    sb.append("\n МАПА кэш");
                    sb.append("  Метод кэш =").append(entrycache.getKey());
                    sb.append(" Cache Value= ").append(entrycache.getValue().getValue());
                    sb.append(" Время жизни =").append(entrycache.getValue().getTimeLife());
                }
                sb.append("    конец списка мап  } ");
                sb.append("   конец снимка}  \n");
            }
            return sb.toString();
        }
        public   void shutDown () {
            cleanerTask.cancel(false);
            executor.shutdown();
            try {
                executor.awaitTermination(3, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                //обработка прерывания
                Thread.currentThread().interrupt();
            }
        }


    }



}
