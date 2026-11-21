-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1
-- Tiempo de generación: 21-11-2026 a las 21:05:11
-- Versión del servidor: 10.4.32-MariaDB
-- Versión de PHP: 8.0.30

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `gestion_vehiculos_db`
--

DELIMITER $$
--
-- Procedimientos
--
CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_cargue_trayecto` (IN `p_id_conductor` VARCHAR(50), IN `p_placa` VARCHAR(20), IN `p_codigo_ruta` VARCHAR(50), IN `p_ubicacion` VARCHAR(255), IN `p_orden_parada` VARCHAR(20), IN `p_latitud` VARCHAR(50), IN `p_longitud` VARCHAR(50), IN `p_usuario` VARCHAR(50), IN `p_id_cargue` BIGINT)   BEGIN
    INSERT INTO trayecto_tmp (
        identificacion_conductor, placa_vehiculo, codigo_ruta, ubicacion, 
        orden_parada, latitud, longitud, usuario_registro, estado, observacion, id_cargue
    ) VALUES (
        p_id_conductor, p_placa, p_codigo_ruta, p_ubicacion, 
        p_orden_parada, p_latitud, p_longitud, p_usuario, 'CARGADO', '', p_id_cargue
    );
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_procesado_trayecto` (IN `p_id_cargue` BIGINT)   BEGIN
    INSERT INTO trayecto (idpersona, idvehiculo, codigo_ruta, ubicacion, orden_parada, latitud, longitud, usuario_registro)
    SELECT 
        p.id,
        v.id,
        TRIM(t.codigo_ruta),
        TRIM(t.ubicacion),
        CAST(t.orden_parada AS UNSIGNED),
        NULLIF(TRIM(t.latitud), ''),
        NULLIF(TRIM(t.longitud), ''),
        IFNULL(NULLIF(TRIM(t.usuario_registro), ''), 'EXCEL_CARGUE')
    FROM trayecto_tmp t
    JOIN persona p ON TRIM(p.Identificacion) = TRIM(t.identificacion_conductor)
    JOIN vehiculo v ON UPPER(TRIM(v.placa)) = UPPER(TRIM(t.placa_vehiculo))
    WHERE t.id_cargue = p_id_cargue
      AND t.estado = 'VALIDADO';

    UPDATE trayecto_tmp
    SET estado = 'PROCESADO',
        observacion = 'Trasladado exitosamente a tabla transaccional Trayecto'
    WHERE id_cargue = p_id_cargue
      AND estado = 'VALIDADO';
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `sp_validacion_trayecto` (IN `p_id_cargue` VARCHAR(50))   BEGIN
    -- 1. Campos obligatorios vacíos
    UPDATE trayecto_tmp 
    SET estado = 'ERROR', 
        observacion = CONCAT(IFNULL(observacion, ''), 'Campos obligatorios vacíos (conductor, placa, ruta, ubicación o parada); ')
    WHERE id_cargue = p_id_cargue 
      AND (IFNULL(TRIM(identificacion_conductor), '') = '' 
           OR IFNULL(TRIM(placa_vehiculo), '') = '' 
           OR IFNULL(TRIM(codigo_ruta), '') = '' 
           OR IFNULL(TRIM(ubicacion), '') = '' 
           OR IFNULL(TRIM(orden_parada), '') = '');

    -- 2. Orden de parada numérico
    UPDATE trayecto_tmp 
    SET estado = 'ERROR', 
        observacion = CONCAT(IFNULL(observacion, ''), 'El orden_parada debe ser numérico; ')
    WHERE id_cargue = p_id_cargue 
      AND orden_parada NOT REGEXP '^[0-9]+$';

    -- 3. Validar Conductor (tipo 'C', licencia vigente y estado 'PO' en vehiculo_conductor)
    UPDATE trayecto_tmp t
    LEFT JOIN persona p ON TRIM(p.Identificacion) = TRIM(t.identificacion_conductor)
    LEFT JOIN vehiculo v ON UPPER(TRIM(v.placa)) = UPPER(TRIM(t.placa_vehiculo))
    LEFT JOIN vehiculo_conductor vc ON vc.persona_id = p.id AND vc.vehiculo_id = v.id
    SET t.estado = 'ERROR',
        t.observacion = CONCAT(IFNULL(t.observacion, ''), 'Conductor inválido, licencia vencida o no tiene estado PO (Puede Operar) con este vehículo; ')
    WHERE t.id_cargue = p_id_cargue
      AND (p.id IS NULL 
           OR UPPER(p.tipo_persona) <> 'C' 
           OR p.fecha_vigencia_licencia < CURRENT_DATE()
           OR vc.estado IS NULL 
           OR UPPER(vc.estado) <> 'PO');

    -- 4. Validar Vehículo y sus documentos
    UPDATE trayecto_tmp t
    LEFT JOIN vehiculo v ON UPPER(TRIM(v.placa)) = UPPER(TRIM(t.placa_vehiculo))
    SET t.estado = 'ERROR',
        t.observacion = CONCAT(IFNULL(t.observacion, ''), 'Vehículo no registrado en el sistema; ')
    WHERE t.id_cargue = p_id_cargue
      AND v.id IS NULL;

    UPDATE trayecto_tmp t
    JOIN vehiculo v ON UPPER(TRIM(v.placa)) = UPPER(TRIM(t.placa_vehiculo))
    JOIN vehiculo_documento vd ON vd.vehiculo_id = v.id
    SET t.estado = 'ERROR',
        t.observacion = CONCAT(IFNULL(t.observacion, ''), 'Vehículo con documentos vencidos o no habilitados; ')
    WHERE t.id_cargue = p_id_cargue
      AND (UPPER(vd.estado) NOT IN ('HABILITADO', 'APROBADO', 'VIGENTE') OR vd.fecha_vencimiento < CURRENT_DATE());

    -- 5. Marcar como VALIDADO los que pasaron todas las reglas
    UPDATE trayecto_tmp
    SET estado = 'VALIDADO',
        observacion = 'Registro válido listo para procesar'
    WHERE id_cargue = p_id_cargue
      AND estado = 'CARGADO';
END$$

DELIMITER ;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `documento_parametrico`
--

CREATE TABLE `documento_parametrico` (
  `id` bigint(20) NOT NULL,
  `codigo` varchar(20) NOT NULL,
  `nombre` varchar(100) NOT NULL,
  `tipos_vehiculo_aplica` varchar(2) NOT NULL,
  `obligatorio` varchar(2) NOT NULL,
  `descripcion` text DEFAULT NULL
) ;

--
-- Volcado de datos para la tabla `documento_parametrico`
--

INSERT INTO `documento_parametrico` (`id`, `codigo`, `nombre`, `tipos_vehiculo_aplica`, `obligatorio`, `descripcion`) VALUES
(1, 'DOC001', 'SOAT', 'AM', 'RR', 'Seguro Obligatorio de Accidentes de Tránsito'),
(2, 'DOC002', 'Técnico Mecánica', 'AM', 'RR', 'Revisión Técnico-Mecánica y de gases'),
(3, 'DOC003', 'Seguro Todo Riesgo', 'AM', 'RR', 'Póliza de seguro voluntario contra todo riesgo');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `persona`
--

CREATE TABLE `persona` (
  `id` bigint(20) NOT NULL,
  `identificacion` varchar(255) NOT NULL,
  `tipo_identificacion` varchar(255) NOT NULL,
  `nombres` varchar(255) NOT NULL,
  `apellidos` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `tipo_persona` varchar(255) NOT NULL,
  `estado` varchar(255) DEFAULT NULL,
  `licencia_conduccion` tinytext DEFAULT NULL,
  `fecha_vigencia_licencia` date DEFAULT NULL
) ;

--
-- Volcado de datos para la tabla `persona`
--

INSERT INTO `persona` (`id`, `identificacion`, `tipo_identificacion`, `nombres`, `apellidos`, `email`, `tipo_persona`, `estado`, `licencia_conduccion`, `fecha_vigencia_licencia`) VALUES
(1, '123456789', 'CC', 'Admin', 'Sistema', 'admin@correo.com', 'A', 'ACTIVO', NULL, '2028-12-31'),
(2, '987654321', 'CC', 'Carlos', 'Pérez', 'carlos.perez@correo.com', 'C', NULL, NULL, NULL),
(3, '11223344', 'CC', 'Laura', 'Mendez', 'laura.mendez@correo.com', 'A', 'ACTIVO', NULL, NULL);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `trayecto`
--

CREATE TABLE `trayecto` (
  `id` bigint(20) NOT NULL,
  `idpersona` bigint(20) NOT NULL,
  `idvehiculo` bigint(20) NOT NULL,
  `codigo_ruta` varchar(255) NOT NULL,
  `ubicacion` varchar(255) NOT NULL,
  `orden_parada` int(11) NOT NULL,
  `latitud` double DEFAULT NULL,
  `longitud` double DEFAULT NULL,
  `usuario_registro` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `trayecto`
--

INSERT INTO `trayecto` (`id`, `idpersona`, `idvehiculo`, `codigo_ruta`, `ubicacion`, `orden_parada`, `latitud`, `longitud`, `usuario_registro`) VALUES
(1, 1, 2, 'RUTA01', 'Parque Bolívar', 1, 4.43889, -75.23222, 'admin'),
(2, 1, 2, 'RUTA01', 'Plaza de Bolívar', 2, 4.442, -75.235, 'admin'),
(3, 1, 2, 'RUTA01', 'Estadio Murillo Toro', 3, 4.432, -75.228, 'admin'),
(4, 1, 2, 'RUTA01', 'Terminal de Transporte', 4, 4.4389, -75.2322, 'admin'),
(5, 1, 2, 'RUTA01', 'Centro Comercial Multicentro', 5, 4.4415, -75.205, 'admin'),
(6, 1, 2, 'RUTA01', 'Parque Murillo Toro', 6, 4.4447, -75.2424, 'admin'),
(9, 1, 2, 'RUTA02', 'Batallon Jaime Rooke, Ibague', 7, 4.43889, -75.23222, NULL);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `trayecto_tmp`
--

CREATE TABLE `trayecto_tmp` (
  `id` bigint(20) NOT NULL,
  `identificacion_conductor` varchar(255) DEFAULT NULL,
  `placa_vehiculo` varchar(255) DEFAULT NULL,
  `codigo_ruta` varchar(255) DEFAULT NULL,
  `ubicacion` varchar(255) DEFAULT NULL,
  `orden_parada` varchar(255) DEFAULT NULL,
  `latitud` varchar(255) DEFAULT NULL,
  `longitud` varchar(255) DEFAULT NULL,
  `usuario_registro` varchar(255) DEFAULT NULL,
  `estado` varchar(255) DEFAULT NULL,
  `observacion` varchar(255) DEFAULT NULL,
  `id_cargue` bigint(20) NOT NULL
) ;

--
-- Volcado de datos para la tabla `trayecto_tmp`
--

INSERT INTO `trayecto_tmp` (`id`, `identificacion_conductor`, `placa_vehiculo`, `codigo_ruta`, `ubicacion`, `orden_parada`, `latitud`, `longitud`, `usuario_registro`, `estado`, `observacion`, `id_cargue`) VALUES
(1, '123456789', 'ABC123', 'RUTA01', 'Terminal de Transporte', '4', '4.4389', '-75.2322', 'admin', 'PROCESADO', 'Trasladado exitosamente a tabla transaccional Trayecto', 202611201753),
(2, '123456789', 'ABC123', 'RUTA01', 'Centro Comercial Multicentro', '5', '4.4415', '-75.2050', 'admin', 'PROCESADO', 'Trasladado exitosamente a tabla transaccional Trayecto', 202611201753),
(3, '123456789', 'ABC123', 'RUTA01', 'Parque Murillo Toro', '6', '4.4447', '-75.2424', 'admin', 'PROCESADO', 'Trasladado exitosamente a tabla transaccional Trayecto', 202611201753),
(4, '999999999', 'ABC123', 'RUT01', 'Parada Conductor Inexistente', '4', '4.4500', '-75.2100', 'admin', 'ERROR', 'Conductor inválido, licencia vencida o no tiene estado PO (Puede Operar) con este vehículo; ', 202611201753),
(5, '123456789', 'ZZZ999', 'RUT01', 'Parada Placa Inexistente', '5', '4.4550', '-75.2150', 'admin', 'ERROR', 'Conductor inválido, licencia vencida o no tiene estado PO (Puede Operar) con este vehículo; Vehículo no registrado en el sistema; ', 202611201753);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `usuario`
--

CREATE TABLE `usuario` (
  `login` varchar(255) NOT NULL,
  `idpersona` bigint(20) NOT NULL,
  `password` varchar(255) NOT NULL,
  `apikey` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `usuario`
--

INSERT INTO `usuario` (`login`, `idpersona`, `password`, `apikey`) VALUES
('as123456789', 1, '12345', '12345'),
('cp987654321', 2, 'ClaveNueva2026', '43097105540445e985d62e667009925c'),
('lm11223344', 3, 'c2941449', 'ec0aa71ac3fa44c282314e2c04897ba9');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `vehiculo`
--

CREATE TABLE `vehiculo` (
  `id` bigint(20) NOT NULL,
  `tipo_vehiculo` varchar(20) NOT NULL,
  `placa` varchar(6) NOT NULL,
  `tipo_servicio` varchar(2) NOT NULL,
  `tipo_combustible` varchar(20) NOT NULL,
  `capacidad_pasajeros` int(11) NOT NULL,
  `color` varchar(7) NOT NULL,
  `modelo` int(11) NOT NULL,
  `marca` varchar(50) NOT NULL,
  `linea` varchar(50) NOT NULL
) ;

--
-- Volcado de datos para la tabla `vehiculo`
--

INSERT INTO `vehiculo` (`id`, `tipo_vehiculo`, `placa`, `tipo_servicio`, `tipo_combustible`, `capacidad_pasajeros`, `color`, `modelo`, `marca`, `linea`) VALUES
(2, 'Automóvil', 'ABC123', 'Pu', 'Gasolina', 5, '#FF0000', 2022, 'Toyota', 'Corolla');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `vehiculo_conductor`
--

CREATE TABLE `vehiculo_conductor` (
  `vehiculo_id` bigint(20) NOT NULL,
  `persona_id` bigint(20) NOT NULL,
  `fecha_asociacion` date NOT NULL,
  `estado` varchar(255) NOT NULL
) ;

--
-- Volcado de datos para la tabla `vehiculo_conductor`
--

INSERT INTO `vehiculo_conductor` (`vehiculo_id`, `persona_id`, `fecha_asociacion`, `estado`) VALUES
(2, 1, '2026-11-20', 'PO');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `vehiculo_documento`
--

CREATE TABLE `vehiculo_documento` (
  `id` bigint(20) NOT NULL,
  `vehiculo_id` bigint(20) NOT NULL,
  `documento_id` bigint(20) NOT NULL,
  `fecha_expedicion` date NOT NULL,
  `fecha_vencimiento` date NOT NULL,
  `estado` varchar(20) NOT NULL,
  `observaciones` varchar(255) DEFAULT NULL,
  `documento_pdf` longblob DEFAULT NULL
) ;

--
-- Volcado de datos para la tabla `vehiculo_documento`
--

INSERT INTO `vehiculo_documento` (`id`, `vehiculo_id`, `documento_id`, `fecha_expedicion`, `fecha_vencimiento`, `estado`, `observaciones`, `documento_pdf`) VALUES
(1, 2, 1, '2026-01-01', '2027-01-01', 'Habilitado', 'Documento revisado y aprobado', 0x4a564245526930784c6a514b4a634f6b77377a4474734f66436a49674d434276596d6f4b50447776544756755a33526f49444d754d433947615778305a584976526d7868644756455a574e765a47552b506e4e30636d5668625170346e4450514d31516f357970554d4641774145496a41774e3951774f39584176395841753958417639584176395841763958417639584176395841763958413d3d);

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `documento_parametrico`
--
ALTER TABLE `documento_parametrico`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `codigo` (`codigo`);

--
-- Indices de la tabla `persona`
--
ALTER TABLE `persona`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `identificacion` (`identificacion`);

--
-- Indices de la tabla `trayecto`
--
ALTER TABLE `trayecto`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idpersona` (`idpersona`),
  ADD KEY `idvehiculo` (`idvehiculo`);

--
-- Indices de la tabla `trayecto_tmp`
--
ALTER TABLE `trayecto_tmp`
  ADD PRIMARY KEY (`id`);

--
-- Indices de la tabla `usuario`
--
ALTER TABLE `usuario`
  ADD PRIMARY KEY (`login`,`idpersona`),
  ADD UNIQUE KEY `uk_usuario_persona` (`idpersona`);

--
-- Indices de la tabla `vehiculo`
--
ALTER TABLE `vehiculo`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `placa` (`placa`);

--
-- Indices de la tabla `vehiculo_conductor`
--
ALTER TABLE `vehiculo_conductor`
  ADD PRIMARY KEY (`vehiculo_id`,`persona_id`),
  ADD KEY `fk_vc_persona` (`persona_id`);

--
-- Indices de la tabla `vehiculo_documento`
--
ALTER TABLE `vehiculo_documento`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fk_vehiculo_doc_vehiculo` (`vehiculo_id`),
  ADD KEY `fk_vehiculo_doc_documento` (`documento_id`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `documento_parametrico`
--
ALTER TABLE `documento_parametrico`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `persona`
--
ALTER TABLE `persona`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `trayecto`
--
ALTER TABLE `trayecto`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- AUTO_INCREMENT de la tabla `trayecto_tmp`
--
ALTER TABLE `trayecto_tmp`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `vehiculo`
--
ALTER TABLE `vehiculo`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `vehiculo_documento`
--
ALTER TABLE `vehiculo_documento`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- Restricciones para tablas volcadas
--

--
-- Filtros para la tabla `trayecto`
--
ALTER TABLE `trayecto`
  ADD CONSTRAINT `trayecto_ibfk_1` FOREIGN KEY (`idpersona`) REFERENCES `persona` (`id`),
  ADD CONSTRAINT `trayecto_ibfk_2` FOREIGN KEY (`idvehiculo`) REFERENCES `vehiculo` (`id`);

--
-- Filtros para la tabla `usuario`
--
ALTER TABLE `usuario`
  ADD CONSTRAINT `fk_usuario_persona` FOREIGN KEY (`idpersona`) REFERENCES `persona` (`id`) ON DELETE CASCADE;

--
-- Filtros para la tabla `vehiculo_conductor`
--
ALTER TABLE `vehiculo_conductor`
  ADD CONSTRAINT `fk_vc_persona` FOREIGN KEY (`persona_id`) REFERENCES `persona` (`id`),
  ADD CONSTRAINT `fk_vc_vehiculo` FOREIGN KEY (`vehiculo_id`) REFERENCES `vehiculo` (`id`);

--
-- Filtros para la tabla `vehiculo_documento`
--
ALTER TABLE `vehiculo_documento`
  ADD CONSTRAINT `fk_vehiculo_doc_documento` FOREIGN KEY (`documento_id`) REFERENCES `documento_parametrico` (`id`),
  ADD CONSTRAINT `fk_vehiculo_doc_vehiculo` FOREIGN KEY (`vehiculo_id`) REFERENCES `vehiculo` (`id`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
