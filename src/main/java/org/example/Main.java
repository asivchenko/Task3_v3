package org.example;


import java.util.concurrent.*;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    public static void main(String[] args) {

        //// изучение https://tproger.ru/translations/java8-concurrency-tutorial-1  планировщик заданий


        Fraction fr= new Fraction(2,3);
        System.out.println(fr.count);
        Fractionable num =Utils.cache(fr);

        System.out.println("num="+num);
        num.setNum(2);

        double value_1= num.doubleValue();// sout сработал

        System.out.println("value_1=" +value_1 + " count="+fr.count);
        double value_2= num.doubleValue();// sout молчит
        System.out.println("value_2=" +value_2 + " count="+fr.count);

        num.setNum(5);



        double value_3=num.doubleValue();// sout сработал
        System.out.println("value_3=" +value_3 + " count="+fr.count);
        double value_4=num.doubleValue();// sout молчит
        System.out.println("value_4=" +value_4 + "count="+fr.count);

        num.setNum(2);

        double value_5 =num.doubleValue();// sout молчит

        System.out.println("value_5=" +value_5 + " count="+fr.count);

        double value_6 =num.doubleValue();// sout молчит
        System.out.println("value_6=" +value_6 + "count="+fr.count);
          try {
              Thread.sleep(5000);
          }
          catch (InterruptedException e) {
              System.out.println("Ошибка sleep ");
              e.printStackTrace();
          }
        double  value_7= num.doubleValue();// sout сработал
        System.out.println("value_7=" +value_6 + "count="+fr.count);
        double  value_8=  num.doubleValue();// sout молчит
        System.out.println("value_8=" +value_6 + "count="+fr.count);
        Utils.shutdown();




        //System.out.printf("Hello and welcome!");

        //  Fraction fr= new Fraction(2,3);
        // создаем кэшируемую версию объекта с помощью  метода cache
        // Fractionable num = Utils.cache(fr);
        /*ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            String threadName = Thread.currentThread().getName();
            System.out.println("Hello " + threadName);
        });  */
/*
        // вывод строки в двух потоках
        Runnable task = () -> {
            String threadName = Thread.currentThread().getName();
            System.out.println("Hello " + threadName);
        };
        System.out.println("22");
        task.run();  // в главном потоке

        Thread thread = new Thread(task);
        System.out.println("111");
        thread.start();    // в отдельном потоке

        System.out.println("Done!");
         */

        /*

        /*
        Runnable runnable = () -> {
            try {
                System.out.println("111");
                String name = Thread.currentThread().getName();
                System.out.println("Foo " + name);
                TimeUnit.SECONDS.sleep(5);
                System.out.println("Bar " + name);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        };

        Thread thread = new Thread(runnable);
        System.out.println("22222");
        thread.start();
        */

/*


        //Вот как будет выглядеть наш первый пример с использованием исполнителя:

        ExecutorService executor = Executors.newSingleThreadExecutor(); //исполнитель с одним потоком.
        executor.submit(() -> {
            String threadName = Thread.currentThread().getName();

            System.out.println("Hello " + threadName);
        });

        //остановим исполнителя
        try {
            System.out.println("attempt to shutdown executor");
            executor.shutdown();
            executor.awaitTermination(5, TimeUnit.SECONDS);
        }
        catch (InterruptedException e) {
            System.err.println("tasks interrupted");
        }
        finally {
            if (!executor.isTerminated()) {
                System.err.println("cancel non-finished tasks");
            }
            executor.shutdownNow();
            System.out.println("shutdown finished");
        }
*/
        /*
        //ScheduledExecutorService способен запускать задачи один или несколько раз с заданным интервалом.
        //        Этот пример показывает, как заставить исполнитель выполнить задачу через три секунды:

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

        Runnable task = () -> System.out.println("Scheduling: " + System.nanoTime());
        ScheduledFuture future = executor.schedule(task, 3, TimeUnit.SECONDS);
        try {
            TimeUnit.MILLISECONDS.sleep(1337);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        long remainingDelay = future.getDelay(TimeUnit.MILLISECONDS);
        System.out.printf("Remaining Delay: %sms", remainingDelay);

      */
/*
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

        Runnable task = () -> {
            try {
                TimeUnit.SECONDS.sleep(2);
                System.out.println("Scheduling: " + System.nanoTime());
            }
            catch (InterruptedException e) {
                System.err.println("task interrupted");
            }
        };
        //scheduleWithFixedDelay().
        // Он работает примерно так же, как и предыдущий, но указанный интервал будет отсчитываться от времени завершения предыдущей задачи.
        executor.scheduleWithFixedDelay(task, 0, 1, TimeUnit.SECONDS);

*/





    }
}