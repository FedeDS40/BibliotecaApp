-- 01_crear_bd.sql
-- Ejecutar conectado a la base "postgres" (usuario con permisos de creación):
--   psql -U postgres -f sql/01_crear_bd.sql

DROP DATABASE IF EXISTS biblioteca;
CREATE DATABASE biblioteca ENCODING 'UTF8';
