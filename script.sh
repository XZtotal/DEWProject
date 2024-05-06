#!/usr/bin/bash
if [ -z $1 ] || [ -z $2 ]
then
	echo "Uso: ./script.sh <DNI de alumno a consultar> <DNI de profesor a consultar>"
	exit
fi

KEY=$(curl -s -X POST "http://localhost:9090/CentroEducativo/login" -H  "accept: text/plain" -H  "Content-Type: application/json" -d "{  \"dni\": \"23456733H\",  \"password\": \"123456\"}" -c cookies.txt -b cookies.txt)
AlumnosYAsignaturas=$(curl -s -X GET "http://localhost:9090/CentroEducativo/alumnosyasignaturas?key="$KEY -H  "accept: application/json" -c cookies.txt -b cookies.txt)
echo "[+] Alumnos y asignaturas"
echo $AlumnosYAsignaturas 
echo -e "\n"

Alumno=$(curl -s -X GET "http://localhost:9090/CentroEducativo/alumnos/"$1"/?key="$KEY -c cookies.txt -b cookies.txt)
echo "[+] Alumno con dni: " $1
echo $Alumno 
echo -e "\n"

AsignaturasAlumno=$(curl -s -X GET "http://localhost:9090/CentroEducativo/alumnos/"$1"/asignaturas/?key="$KEY -c cookies.txt -b cookies.txt)
echo "[+] Asignaturas del alumno"
echo $AsignaturasAlumno
echo -e "\n"

echo "[+] Profesores"
Profesores=$(curl -s -X GET "http://localhost:9090/CentroEducativo/profesores?key="$KEY -H  "accept: application/json" -c cookies.txt -b cookies.txt)
echo $Profesores
echo -e "\n"

echo "[+] Profesor con dni: " $2
Profesor=$(curl -s -X GET "http://localhost:9090/CentroEducativo/profesores/"$2"/?key="$KEY -c cookies.txt -b cookies.txt)
echo $Profesor
echo -e "\n"

AsignaturasProfesor=$(curl -s -X GET "http://localhost:9090/CentroEducativo/profesores/"$2"/asignaturas/?key="$KEY -c cookies.txt -b cookies.txt)
echo "[+] Asignaturas del profesor"
echo $AsignaturasProfesor
echo -e "\n"
