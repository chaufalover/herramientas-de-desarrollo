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
Spring Boot es un módulo de Spring que permite, de manera sencilla, crear aplicaciones autónomas y listas para producción basadas en Java, eliminando gran parte de la configuración manual.” (Ramírez Pérez, 2020)

Esta cita muestra la ventaja clave de Spring Boot que es contar con una mejor configuración y un mejor arranque en la aplicaciones. En nuestro proyecto tener un framework para realizar las funcionalidades como operaciones, autenticación por QR y monitoreo de colas, es una gran ventaja y nos permite solo enfocarnos en la logica.

También una tesis de fin de máster analizó Spring Boot como framework de aplicaciones web empresariales y resaltó su impacto positivo en la productividad y uso de librerías incluidas.

# Mysql
"MySQL es un sistema de gestión de bases de datos relacionales (RDBMS) de código abierto que se utiliza para almacenar y gestionar datos. Su fiabilidad, rendimiento, escalabilidad y facilidad de uso lo convierten en una opción popular para los desarrolladores.” (Erickson, 2024)

Esta cita nos reafirma nuestra eleccion de mysql para nuestro proyecto. En el entorno bancario que estamos desarrollando, donde se manejarán múltiples clientes, cuentas, transacciones y altos volúmenes de operaciones, necesitamos un sistema que ofrezca consistencia, escalabilidad y buen rendimiento. Mysql cumple perfectamente estas exigencias

En nuestro sistema, MySQL será la capa de gestión de datos donde residirán todas las entidades críticas como cliente, cuenta, tipo de cuenta, tipo de transacción, transacción, empleado y sus relaciones.

# Bootstrap
“Bootstrap es un framework de desarrollo web gratuito y de código abierto. Está diseñado para facilitar el proceso de desarrollo de los sitios web responsivos y orientados a los dispositivos móviles, proporcionando una colección de sintaxis para diseños de plantillas.” (Deyimar A., 2025)

Para nuestro sistema bancario la elección de Bootstrap nos parece muy adecuada porque necesitamos que la aplicación web sea usable en múltiples dispositivos como computadora, tabletas, teléfonos móviles; y que tenga una apariencia profesional y consistente sin tener que diseñar todos los componentes desde cero.

En la nuestra plataforma la usaremos para construir la interfaz de nuestro proyecto. Asimismo con esto podremos asegurar que se adapte a los diferentes tamaños de los dispositivos.

# Thymeleaf

“Thymeleaf es un moderno motor de plantillas del lado del servidor para entornos web y autónomos, capaz de procesar HTML, XML, JavaScript, CSS e incluso texto plano.”

Como lo indica en estea cita Thymeleaf es una herramienta clave porque conecta directamente con el backend como lo puede ser spring boot y nos permite generar vistas HTML dinámicas desde los datos de nuestro sistema.

En nuestro sistema web lo usaremos para gestionar los distintintos aprtados que tenemos como lo son registro de clientes, operacines, autentificación, servicios y visualización de los empleados. 




