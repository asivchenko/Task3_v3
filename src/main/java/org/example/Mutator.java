package org.example;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)  // RUNTIME - аннотация хранится в .class файле и доступна во время выполнения программы.
// Аннотация @Retention позволяет указать жизненный цикл аннотации:
// будет она присутствовать только в исходном коде, в скомпилированном файле,
//  или она будет также видна и в процессе выполнения. Выбор нужного типа зависит
// от того, как вы хотите использовать аннотацию, например,
//  генерировать что-то побочное из исходных кодов, или в процессе выполнения стучаться к классу через reflection.
@Target(ElementType.METHOD)         // область применения
// Аннотация @Target указывает, что именно мы можем пометить этой аннотацией,
// это может быть поле, метод, тип и т.д.
// + есть @Documented @Inherited
public @interface Mutator {

}