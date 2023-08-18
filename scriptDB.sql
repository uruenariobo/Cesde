-- Create the Docente table
CREATE TABLE Docente (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    documento VARCHAR(20),
    correo VARCHAR(100)
);

-- Create the Curso table
CREATE TABLE Curso (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT,
    duracion INT,
    precio DECIMAL(10, 2),
    fechaInicio DATETIME,
    docente_id INT,
    FOREIGN KEY (docente_id) REFERENCES Docente(id)
);
