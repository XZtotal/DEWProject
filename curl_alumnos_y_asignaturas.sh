#Obtener listado de alumnos y asignaturas
KEY=$(curl -s -X POST "http://localhost:9090/CentroEducativo/login" -H  "accept: text/plain" -H  "Content-Type: application/json" -d "{  \"dni\": \"23456733H\",  \"password\": \"123456\"}" -c cookies.txt -b cookies.txt)
echo $KEY
AlumnosYAsignaturas=$(curl -s -X GET "http://localhost:9090/CentroEducativo/alumnosyasignaturas?key="$KEY -H  "accept: application/json" -c cookies.txt -b cookies.txt)
echo $AlumnosYAsignaturas