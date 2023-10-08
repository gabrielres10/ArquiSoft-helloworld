# Estrategias para probar el Desempeño del programa como atributo de calidad
## Medición del tiempo en Nanosegundos (ns)
Para verificar la velocidad de respuesta proponemos la idea de usar una librería de java para correrla en segundo plano, de modo que cuando un cliente mande una solicitud se ejecute la operación y justo debajo de la declaración del método manejador de solicitudes, mostrar el tiempo transcurrido hasta entonces. 

Para la prueba de estrés podemos tener varios equipos con el programa del cliente conectados al mismo tiempo, de modo que se podrán resolver 2 cuestiones principalmente: ¿El programa es capaz de soportar todos los equipos de la sala al tiempo? y ¿En qué medida afecta la cantidad de clientes conectados a la velocidad de respuesta?


## _Integrantes:_
Este trabajo fue realizado por Juan Camilo González Torres en conjunto con Gabriel Esteban Restrepo Giraldo