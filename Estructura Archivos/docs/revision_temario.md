# Revisión de construcciones Java utilizadas contra la referencia

El proyecto evita APIs y herramientas no identificadas en la referencia, como Streams de colecciones, `Optional`, `record`, `var`, JUnit, Mockito, Maven, Gradle y `enum.valueOf()`.

Construcciones principales utilizadas:

- Clases, constructores, atributos, métodos, `this`, `public/private/protected`.
- Herencia con `extends`, clases abstractas, métodos abstractos, `@Override`.
- Interfaces e `implements`.
- Enums.
- `instanceof` y cast.
- `ArrayList`, `List`, `HashMap`, `Map`, `Collections.sort`.
- `Comparable`, `Comparator`, `Integer.compare`, `Double.compare`.
- `Scanner`, `Integer.parseInt`, `Double.parseDouble`.
- `try`, `catch`, `finally`, `throw`, `throws`, excepción propia.
- `Serializable`, `FileInputStream`, `FileOutputStream`, `ObjectInputStream`, `ObjectOutputStream`.
- `FileWriter`, `BufferedWriter`, escritura en modo append.
- Métodos de `String` presentes en la referencia: `length`, `charAt`, `equals`, `trim`, `isBlank`, `split`.

### Apartamiento deliberado del UML de Etapa 1

No se usa la API `java.time`; se sustituyó por clases propias `Fecha`, `Hora` y `FechaHora`. El UML de Etapa 2 refleja este cambio.
