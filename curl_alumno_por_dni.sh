#Obtener alumno especificando el DNI por la linea de comandos
KEY=$(curl -s -X POST "http://localhost:9090/CentroEducativo/login" -H  "accept: text/>
alumno=$(curl -s -X GET "http://localhost:9090/CentroEducativo/alumnos/"$1"/?key="$KEY>
echo $alumno