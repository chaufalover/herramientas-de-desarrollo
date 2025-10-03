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



