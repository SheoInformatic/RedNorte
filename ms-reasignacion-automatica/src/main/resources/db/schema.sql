-- Ejecutar manualmente en MySQL antes de arrancar el microservicio
-- mysql -u root -p < schema.sql

CREATE DATABASE IF NOT EXISTS rednorte_reasignacion
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE rednorte_reasignacion;

-- -------------------------------------------------------
-- Usuario para el microservicio
-- -------------------------------------------------------
-- CREATE USER IF NOT EXISTS 'rednorte_user'@'localhost' IDENTIFIED BY 'rednorte_pass';
-- GRANT ALL PRIVILEGES ON rednorte_reasignacion.* TO 'rednorte_user'@'localhost';
-- FLUSH PRIVILEGES;

-- -------------------------------------------------------
-- CITAS
-- -------------------------------------------------------
CREATE TABLE IF NOT EXISTS citas (
    id               BIGINT        NOT NULL AUTO_INCREMENT,
    paciente_id      BIGINT        NOT NULL,
    paciente_rut     VARCHAR(20),
    paciente_nombre  VARCHAR(120),
    fecha_atencion   DATE          NOT NULL,
    centro_atencion  VARCHAR(30)   NOT NULL
        COMMENT 'HOSPITAL_CENTRAL | CENTRO_NORTE | CLINICA_COMUNITARIA',
    especialidad     VARCHAR(30)   NOT NULL
        COMMENT 'MEDICINA_GENERAL | TRAUMATOLOGIA | PEDIATRIA | DERMATOLOGIA',
    estado           VARCHAR(20)   NOT NULL DEFAULT 'RESERVADA'
        COMMENT 'RESERVADA | CANCELADA | COMPLETADA | LIBRE',
    created_at       DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (id),
    INDEX idx_citas_paciente          (paciente_id),
    INDEX idx_citas_centro_esp_fecha  (centro_atencion, especialidad, fecha_atencion),
    INDEX idx_citas_estado            (estado),
    INDEX idx_citas_fecha             (fecha_atencion)
);

-- -------------------------------------------------------
-- LISTA_ESPERA
-- -------------------------------------------------------
CREATE TABLE IF NOT EXISTS lista_espera (
    id                    BIGINT        NOT NULL AUTO_INCREMENT,
    paciente_id           BIGINT        NOT NULL,
    paciente_rut          VARCHAR(20),
    paciente_nombre       VARCHAR(120),
    diagnostico           VARCHAR(255),
    centro_atencion       VARCHAR(30)   NOT NULL,
    especialidad          VARCHAR(30)   NOT NULL,
    fecha_preferida_desde DATE,
    prioridad             INT           NOT NULL DEFAULT 100,
    activo                TINYINT(1)    NOT NULL DEFAULT 1,
    created_at            DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at            DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (id),
    UNIQUE KEY uc_paciente_centro_especialidad (paciente_id, centro_atencion, especialidad),
    INDEX idx_le_centro_esp_activo  (centro_atencion, especialidad, activo),
    INDEX idx_le_prioridad          (prioridad),
    INDEX idx_le_paciente           (paciente_id)
);
