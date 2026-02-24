-- 1. Crear tabla Clientes (Hereda de Persona)
CREATE TABLE clientes (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    apellido VARCHAR(255), -- Actualmente opcional para permitir migración de datos existentes
    direccion VARCHAR(255) NOT NULL,
    telefono VARCHAR(255) NOT NULL,
    cliente_id VARCHAR(255) NOT NULL UNIQUE,
    contrasena VARCHAR(255) NOT NULL,
    estado BOOLEAN NOT NULL DEFAULT TRUE
);

-- 2. Crear tabla Cuentas
CREATE TABLE cuentas (
    id BIGSERIAL PRIMARY KEY,
    numero_cuenta VARCHAR(255) NOT NULL UNIQUE,
    tipo_cuenta VARCHAR(255) NOT NULL,
    saldo_inicial DECIMAL(15, 2) NOT NULL,
    estado BOOLEAN NOT NULL DEFAULT TRUE,
    cliente_id BIGINT NOT NULL,
    CONSTRAINT fk_cliente FOREIGN KEY (cliente_id) REFERENCES clientes(id) ON DELETE CASCADE
);

-- 3. Crear tabla Movimientos
CREATE TABLE movimientos (
    id BIGSERIAL PRIMARY KEY,
    fecha DATE NOT NULL,
    tipo_movimiento VARCHAR(255) NOT NULL,
    valor DECIMAL(15, 2) NOT NULL,
    saldo DECIMAL(15, 2) NOT NULL,
    cuenta_id BIGINT NOT NULL,
    CONSTRAINT fk_cuenta FOREIGN KEY (cuenta_id) REFERENCES cuentas(id) ON DELETE CASCADE
);

-- Registros de Prueba (Seed Data)
INSERT INTO clientes (nombre, apellido, direccion, telefono, cliente_id, contrasena, estado) 
VALUES ('Jose', 'Lema', 'Otavalo sn y principal', '098254785', 'jose789', '1234', true);

INSERT INTO clientes (nombre, apellido, direccion, telefono, cliente_id, contrasena, estado) 
VALUES ('Marianela', 'Montalvo', 'Amazonas y NNUU', '097548965', 'mari234', '5678', true);

INSERT INTO clientes (nombre, apellido, direccion, telefono, cliente_id, contrasena, estado) 
VALUES ('Juan', 'Osorio', '13 junio y Equinoccial', '098874587', 'juan456', '1245', true);

-- Cuentas de prueba
INSERT INTO cuentas (numero_cuenta, tipo_cuenta, saldo_inicial, estado, cliente_id) 
VALUES ('478758', 'Ahorros', 2000.00, true, 1);

INSERT INTO cuentas (numero_cuenta, tipo_cuenta, saldo_inicial, estado, cliente_id) 
VALUES ('225487', 'Corriente', 100.00, true, 2);

INSERT INTO cuentas (numero_cuenta, tipo_cuenta, saldo_inicial, estado, cliente_id) 
VALUES ('495878', 'Ahorros', 0.00, true, 3);

INSERT INTO cuentas (numero_cuenta, tipo_cuenta, saldo_inicial, estado, cliente_id) 
VALUES ('496825', 'Ahorros', 540.00, true, 2);

-- 4. Registros de Movimientos (Seed Data)
-- Movimientos para cuenta 478758 (Jose Lema)
INSERT INTO movimientos (fecha, tipo_movimiento, valor, saldo, cuenta_id) 
VALUES (CURRENT_DATE - INTERVAL '5 days', 'RETIRO', -575.00, 1425.00, 1);

INSERT INTO movimientos (fecha, tipo_movimiento, valor, saldo, cuenta_id) 
VALUES (CURRENT_DATE - INTERVAL '2 days', 'DEPOSITO', 600.00, 2025.00, 1);

-- Movimientos para cuenta 225487 (Marianela Montalvo)
INSERT INTO movimientos (fecha, tipo_movimiento, valor, saldo, cuenta_id) 
VALUES (CURRENT_DATE - INTERVAL '3 days', 'DEPOSITO', 150.00, 250.00, 2);

-- Movimientos para cuenta 495878 (Juan Osorio)
INSERT INTO movimientos (fecha, tipo_movimiento, valor, saldo, cuenta_id) 
VALUES (CURRENT_DATE - INTERVAL '1 day', 'DEPOSITO', 150.00, 150.00, 3);

-- Movimientos para cuenta 496825 (Marianela Montalvo)
INSERT INTO movimientos (fecha, tipo_movimiento, valor, saldo, cuenta_id) 
VALUES (CURRENT_DATE - INTERVAL '1 day', 'RETIRO', -540.00, 0.00, 4);
