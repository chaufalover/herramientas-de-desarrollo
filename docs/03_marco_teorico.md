## Marco Teorico

---

# Git
Según Astigarraga y Cruz-Alonso (2022) es un sistema de control de versiones, permite monitorear el progreso de un proyecto capturando sucesivos estados del mismo. Este mecanismo posibilita rastrear de manera precisa el historial de cambios y el progreso completo del trabajo.

Git se emplea mediante un ciclo de trabajo donde los cambios se preparan mediante el comando "git add" y luego se guardan instantaneamente con "git commit" dentro de la rama presente, permitiendo desarrollar funcionalidades de forma aislada a la rama principal. Asimismo, sus ventajas incluyen la trazabilidad completa del historial, la capacidad de trabajar en equipo de manera paralela y sin interferencias, la operacion local sin depender de internet y la seguridad sobre cada modificación o cambio realizado.

Para el desarrollo del sistema web de gestión segura de colas en entidades bancarias, se utilizará Git con el objetivo de optimizar el trabajo colaborativo, mantener un historial de cambios organizado y garantizar la trazabilidad de todas las modificaciones realizadas.

---

# Github
GitHub es una de las plataformas de desarrollo colaborativo más importantes a nivel global. En ella, los usuarios pueden almacenar y compartir sus proyectos, los cuales superan los 96 millones. La plataforma registra automáticamente las actividades de los usuarios, como la creación de proyectos, la copia de estos o la adición de nuevos archivos, lo que facilita el control de versiones. Además de estas funciones técnicas, GitHub promueve la interacción social al permitir que los usuarios muestren su aprecio por los proyectos o se suscriban para seguir sus actualizaciones (Al-Rubaye y Sukthankar, 2020).

La plataforma Github se utiliza para gestionar el ciclo de vida del software mediante repositorios cloud donde se almacena el código, utilizando ramas para aislar características y pull requests para integrarlas de forma controlada. Esto ofrece ventajas clave como el control preciso de versiones, colaboración simultánea sin conflictos, revisión sistemática de código y despliegue continuo, esenciales para proyectos escalables.

La implementación de GitHub en el desarrollo de nuestro Sistema Web es fundamental, ya que facilitará la colaboración remota del equipo, permitiendo un trabajo simultáneo y organizado mediante el control de versiones, lo que optimizará el ciclo de desarrollo del proyecto.

---

# MVC
Segun Enriquez et al., (2023) señala que el MVC es un patron arquitectonico que plantea dividir el aplicativo en modulos llamados modelo, vista y conmtrolador, con la finalidad de que esten claramente diferenciados y tengan una estrucutura definida. Asimismo, la separacion en modulos ayuda a crear un software fuerte, manetnible y escalable.

El patron arquitectonico MVC se emplea mediante el Modelo que representa la logica de negocio y el acceso a datos, la Vista que es la interfaz del usuario (UI) y el Controlador que hace el rol de intermediario entre la Vista y el Modelo. Sus ventajas son: la separacion de responsabilidades, mantenimiento del codigo y el crecimiento del aplicativo.

El uso del patron MVC en el presente Proyecto Web es primordial para el desarrollo del mismo, porque nos facilitara la implementacion de nuevos modulos, facilitar la correcion de errores y facilitar el trabajo en equipo.

---

# JPA
Spring Data JPA es un componente del ecosistema Spring que facilita el acceso y la gestión de datos persistentes en aplicaciones Java, basándose en la especificación JPA para el mapeo objeto-relacional (Darzu, 2024).

El uso de JPA es para simplificar el acceso y manipulacion de datos en aplicaciones Java, a traves de repositorios, sin necesidad de escribir consultas SQL complejas. Sus principales ventajas son la reduccion signficativa de codigo, una integracion fluida con el ecosistema de Spring, consultas derivadas del nombre de los metodos y facilitar eld esarrrollo de capas de persistencia.

En el sistema web que se desarrollara, se opto por utilizar Spring Data JPA con el proposito de simplificar el acceso y la gestion de datos dentro de la aplicacion, aprovechando el mapeo objeto-relacional (ORM) y el uso de repositorios que automatizan las operaciones de persistencia.

# Spring Boot
Elegí usar Spring Boot porque automatiza mucha de la configuración pesada que normalmente requiere una aplicación Java con Spring. Un estudio de 2023 sobre desarrollo de backend destaca que Spring Boot reduce el “código repetitivo” y agiliza la integración con JPA para persistencia de datos. 

En mi proyecto, Spring Boot me permite arrancar rápido, integrar controladores HTTP, servicios y conectar fácilmente con MySQL mediante Spring Data JPA. Además, facilita un patrón estructurado para escalabilidad futura.

También una tesis de fin de máster analizó Spring Boot como framework de aplicaciones web empresariales y resaltó su impacto positivo en la productividad y uso de librerías incluidas.

---

#Spring REST APIs
REST o Representational State Transfer es un estilo de Arquitectura al momento de realizar una comunicación entre cliente y servidor (Cecilio Álvarez, 2023) que nos permite acciones adecuadas (GET, POST, PUT, DELETE, y otras), almacenamiento en caché, redirección y envio, seguridad (cifrado y autenticación).

Se utilizará para desarrollar los servicios backend responsables de la comunicación entre los diferentes módulos del sistema. Permitirá gestionar el registro y acceso mediante código QR y validación biométrica, así como el módulo de monitoreo con inteligencia artificial para la detección de personas y cálculo del tiempo de atención. 

Ofrece alta escalabilidad y un mantenimiento sencillo Además, su uso del protocolo HTTP estándar asegura compatibilidad con diferentes lenguajes y plataformas, favoreciendo un ecosistema flexible y moderno.

#Spring Security
Spring Security Framework es un marco de Java de código abierto robusto, altamente personalizable, completo y extensible que admite autenticación y autorización (Andreas, 2022)

Este framework nos permitirá proteger los endpoints REST contra accesos no autorizados, implementar roles y privilegios (Administrador, usuario), integrar la autenticación biométrica y el QR como métodos de acceso seguro

Este proporciona un alto nivel de seguridad mediante una gestión avanzada de autenticación y autorización. Su integración nativa con Spring Boot simplifica el desarrollo y la administración de sesiones. Además, ofrece protección contra ataques comunes como CSRF, XSS o inyección de código, fortaleciendo la defensa general del sistema.


#Java
Java es un lenguaje de programación orientado a objetos, multiplataforma ya que se ejecuta en diversos dispositivos (Zuleyka Mesa, 2022), además posee una amplia documentación de manuales en el cual se muestran sus funciones y prestaciones de las diferentes APIS de programación.

Java será el lenguaje principal para el desarrollo del backend. Con él se implementarán los servicios REST y las conexiones con la base de datos

Es un lenguaje robusto y orientado a objetos. ACuenta con un extenso ecosistema de librerías y frameworks, como Spring Boot, Hibernate y Maven. Su portabilidad, al ejecutarse en cualquier entorno con JVM, lo convierte en una opción versátil y duradera.

#Python
Es un lenguaje de programación informático que se utiliza para crear sitios web y software, automatizar tareas y realizar análisis de datos (Coursera Staff, 2023), al ser un lenguaje de propósito general, puede ser utilizado para crear una variedad de programas diferentes y no está especializado en ningún problema en específico.

Python será empleado en el módulo de inteligencia artificial, encargado de procesar y analizar datos provenientes de video e imágenes. Este módulo permitirá detectar el número de personas en la fila mediante técnicas de visión computacional y calcular el tiempo promedio de atención de los usuarios. Los resultados generados serán enviados al backend desarrollado con Spring REST para su almacenamiento y visualización.

Dispone de una amplia gama de librerías para inteligencia artificial y visión por computadora. Su sintaxis sencilla y claridad en el desarrollo facilitan la implementación de algoritmos de análisis. Además, presenta una gran interoperabilidad con otros lenguajes, lo que permite integrarse fácilmente con Java a través de servicios REST.
