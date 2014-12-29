DROP TABLE IF EXISTS Books, Authors, Testing, Images;

CREATE TABLE IF NOT EXISTS Authors(
  Id INT PRIMARY KEY AUTO_INCREMENT,
  Name VARCHAR(25)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS Books (
  Id INT PRIMARY KEY AUTO_INCREMENT,
  AuthorId INT, Title VARCHAR(100),
  FOREIGN KEY (AuthorId) REFERENCES Authors (Id)
    ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS Testing(
  Id INT
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS Images(
  Id INT PRIMARY KEY AUTO_INCREMENT,
  Data MEDIUMBLOB
);

INSERT INTO Authors(Id, Name) VALUES(1, 'Jack London');
INSERT INTO Authors(Id, Name) VALUES(2, 'Honore de Balzac');
INSERT INTO Authors(Id, Name) VALUES(3, 'Lion Feuchtwanger');
INSERT INTO Authors(Id, Name) VALUES(4, 'Emile Zola');
INSERT INTO Authors(Id, Name) VALUES(5, 'Truman Capote');

INSERT INTO Books(Id, AuthorId, Title) VALUES(1, 1, 'Call of the Wild');
INSERT INTO Books(Id, AuthorId, Title) VALUES(2, 1, 'Martin Eden');
INSERT INTO Books(Id, AuthorId, Title) VALUES(3, 2, 'Old Goriot');
INSERT INTO Books(Id, AuthorId, Title) VALUES(4, 2, 'Cousin Bette');
INSERT INTO Books(Id, AuthorId, Title) VALUES(5, 3, 'Jew Suess');
INSERT INTO Books(Id, AuthorId, Title) VALUES(6, 4, 'Nana');
INSERT INTO Books(Id, AuthorId, Title) VALUES(7, 4, 'The Belly of Paris');
INSERT INTO Books(Id, AuthorId, Title) VALUES(8, 5, 'In Cold blood');
INSERT INTO Books(Id, AuthorId, Title) VALUES(9, 5, 'Breakfast at Tiffany');