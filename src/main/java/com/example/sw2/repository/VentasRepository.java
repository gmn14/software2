package com.example.sw2.repository;
import com.example.sw2.constantes.VentasId;
import com.example.sw2.dto.DatosGestorVentasDto;
import com.example.sw2.dto.DatosProductoVentaDto;
import com.example.sw2.dtoReportes.ReportesArticuloDto;
import com.example.sw2.dtoReportes.ReportesClienteDto;
import com.example.sw2.dtoReportes.ReportesComunidadDto;
import com.example.sw2.dtoReportes.ReportesSedesDto;
import com.example.sw2.dtoReportes.ReportesTotalDto;
import com.example.sw2.entity.Ventas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

@Repository
public interface VentasRepository extends JpaRepository<Ventas, Integer> {

    Optional<Ventas> findById(VentasId v);

    Optional<Ventas> findByIdventasAndConfirmado(Integer i, Boolean conf);

    List<Ventas> findByVendedor_IdusuariosAndConfirmado(int id, boolean confirmado);

    @Query(value = "select distinct year(fecha) from Ventas", nativeQuery = true)
    List<Integer> getYears();

    List<Ventas> findVentasByConfirmado(boolean b);

    Optional<Ventas> findByIdventasAndConfirmadoAndId_Tipodocumento(Integer idventas, Boolean confirmado,  Integer id_tipodocumento);

    void deleteById(VentasId id);

    @Query(value="select v.productoinventario as codigoproducto, p.nombre as nombreproducto, \n" +
            "\tc.nombre as comunidadproducto, i.tamanho as tamanhoproducto,\n" +
            "\ti.color as colorproducto, i.foto as fotoproducto,sum(v.cantidad) as cantidadventa,\n" +
            "\tsum(v.precio_venta) as venta\n" +
            "\tFROM Ventas v\n" +
            "\tinner join Inventario i on (v.productoinventario = i.codigo_inventario)\n" +
            "\tinner join Comunidades c on (i.comunidad = c.codigo)\n" +
            "\tinner join Productos p on (i.producto = p.codigonom)\n" +
            "\twhere v.confirmado = 1\n" +
            "\tgroup by v.productoinventario, p.nombre",
            nativeQuery = true)
    List<DatosProductoVentaDto> obtenerDatosPorProducto();

    @Query(value="SELECT p.nombre as nombreproducto, p.codigonom as codigoproducto,\n" +
            "v.tipodocumento as tipodocumento , v.numerodocumento as numerodocumento, \n" +
            "v.nombrecliente as nombrecliente, v.ruc_dni as rucdni, \n" +
            "v.cantidad as cantidadventa, v.precio_venta as precioventa, \n" +
            "v.fecha as fechaventa, v.lugarventa as lugarventa FROM Ventas v \n" +
            "INNER JOIN Inventario i ON (v.productoinventario = i.codigo_inventario)\n" +
            "INNER JOIN Productos p ON (i.producto = p.codigonom)",
            nativeQuery = true)
    List<DatosGestorVentasDto> obtenerDatosGestorVentas();

    @Query(value="SELECT COUNT(idventas) FROM mosqoy.Ventas WHERE vendedor = ?1",nativeQuery=true)
    String cantVentasPorGestor(int usuario);

    @Query(value="SELECT COUNT(idventas) FROM mosqoy.Ventas v INNER JOIN Usuarios u ON (v.vendedor = u.dni) WHERE u.rol = ?1",nativeQuery=true)
    String cantVentasTotalesDeGestores(int rol);

    @Query(value="SELECT SUM(v.cantidad) FROM mosqoy.Ventas v INNER JOIN Usuarios u ON (v.vendedor = u.dni) WHERE u.rol = ?1",nativeQuery=true)
    String cantProductosVendidosPorGestor(int rol);

    @Query(value="SELECT ven.* FROM Ventas ven WHERE ven.vendedor = ?1",nativeQuery=true)
    List<Ventas> buscarPorGestor(int gestor);

    List<Ventas> findByVendedor_Idusuarios(int dni);

    @Query(value = "SELECT ven.* FROM Ventas ven INNER JOIN Usuarios usu ON (ven.vendedor = usu.dni) WHERE usu.rol = ?1",
            nativeQuery = true)
    List<Ventas> buscarVentasDeAdmin(int rol);

    @Procedure(name = "dev_stock_inv")
    void dev_stock_inv(int cant_devol, String codigo);

    @Query(value="SELECT * FROM mosqoy.Ventas ven WHERE YEAR(ven.fecha_creacion) = ?1",nativeQuery=true)
    List<ReportesTotalDto> obtenerReporteAnual(int anho);

    @Query(value="SELECT ven.* FROM mosqoy.Ventas ven, mosqoy.Inventario inv, mosqoy.Productos pro WHERE ven.productoinventario=inv.codigo_inventario AND inv.producto=pro.codigonom AND YEAR(ven.fecha) = ?1 AND pro.codigonom = ?2",nativeQuery=true)
    List<ReportesTotalDto> obtenerReporteAnualxProducto(int anho, String codProd);

    @Query(value="SELECT ven.* FROM mosqoy.Ventas ven, mosqoy.Inventario inv, mosqoy.Comunidades com WHERE ven.productoinventario = inv.codigo_inventario AND inv.comunidad = com.codigo AND YEAR(ven.fecha) = ?1 AND com.codigo = ?2",nativeQuery=true)
    List<ReportesTotalDto> obtenerReporteAnualxComunidad(int anho, String codCom);

    @Query(value="SELECT ven.* FROM mosqoy.Ventas ven WHERE YEAR(ven.fecha) = ?1 AND ven.nombrecliente = ?2",nativeQuery=true)
    List<ReportesTotalDto> obtenerReporteAnualxNombreCliente(int anho, String nomCliente);
    //////FIN REPORTES ANUALES

    @Query(value = "select p.nombre as nombreproducto, c.nombre as comunidadproducto, i.tamanho as tamanhoproducto, i.color as colorproducto, i.foto as fotoproducto,\n" +
            "            v.fecha as fechaventa,sum(v.cantidad) as cantidadventa, v.precio_venta as precioventa, v.productoinventario as codigoproducto\n" +
            "            FROM Ventas v inner join Inventario i on (v.productoinventario = i.codigo_inventario)\n" +
            "            inner join Comunidades c on (i.comunidad = c.codigo) inner join Productos p on (i.producto = p.codigonom) WHERE YEAR(fecha) = YEAR(CURRENT_DATE - INTERVAL 1 MONTH)\n" +
            "AND MONTH(fecha) = MONTH(CURRENT_DATE - INTERVAL 1 MONTH) group by v.productoinventario", nativeQuery = true)
    List<DatosProductoVentaDto> obtenerDatosPorProductoUltimoMes();

    @Query(value = "select p.nombre as nombreproducto, c.nombre as comunidadproducto, i.tamanho as tamanhoproducto, i.color as colorproducto, i.foto as fotoproducto,\n" +
            "            v.fecha as fechaventa,sum(v.cantidad) as cantidadventa, v.precio_venta as precioventa, v.productoinventario as codigoproducto\n" +
            "            FROM Ventas v inner join Inventario i on (v.productoinventario = i.codigo_inventario)\n" +
            "            inner join Comunidades c on (i.comunidad = c.codigo) inner join Productos p on (i.producto = p.codigonom) WHERE YEAR(fecha) = YEAR(CURRENT_DATE - INTERVAL 3 MONTH)\n" +
            "AND MONTH(fecha) <= MONTH(CURRENT_DATE - INTERVAL 1 MONTH) and month(fecha) >= month(current_date - interval 3 month) group by v.productoinventario", nativeQuery = true)
    List<DatosProductoVentaDto> obtenerDatosPorProductoUltimoTrimestre();


    /////////// ventas de una comunidad por anho especifico

    @Query(value="SELECT * FROM mosqoy.Ventas ven WHERE YEAR(ven.fecha) = ?1",nativeQuery=true)
    List<ReportesTotalDto> obtenerVentasGENERALESPorAnho(int anho);

    /////////// FIN ventas de una comunidad por anho especifico

    /////////// ventas de una comunidad por anho especifico

    @Query(value="SELECT ven.* FROM mosqoy.Ventas ven, mosqoy.Inventario inv, mosqoy.Comunidades com WHERE ven.productoinventario = inv.codigo_inventario AND inv.comunidad = ?1 AND YEAR(ven.fecha) = ?2",nativeQuery=true)
    List<ReportesTotalDto> obtenerVentasDeComunidadPorAnho(String comunidadId, int anho);

    /////////// FIN ventas de una comunidad por anho especifico

    /////////// ventas de una comunidad por TRIMESTRE especifico en un anho especifico

    @Query(value="SELECT ven.* FROM mosqoy.Ventas ven, mosqoy.Inventario inv, mosqoy.Comunidades com WHERE ven.productoinventario = inv.codigo_inventario AND inv.comunidad = ?1 AND MONTH(ven.fecha) > 0 AND MONTH(ven.fecha) < 4 AND YEAR(ven.fecha) = ?2",nativeQuery=true)
    List<ReportesTotalDto> obtenerVentasDeComunidadDelPRIMERTrimestre(String comunidadId, int anho);

    @Query(value="SELECT ven.* FROM mosqoy.Ventas ven, mosqoy.Inventario inv, mosqoy.Comunidades com WHERE ven.productoinventario = inv.codigo_inventario AND inv.comunidad = ?1 AND MONTH(ven.fecha) > 3 AND MONTH(ven.fecha) < 7 AND YEAR(ven.fecha) = ?2",nativeQuery=true)
    List<ReportesTotalDto> obtenerVentasDeComunidadDelSEGUNDOTrimestre(String comunidadId, int anho);

    @Query(value="SELECT ven.* FROM mosqoy.Ventas ven, mosqoy.Inventario inv, mosqoy.Comunidades com WHERE ven.productoinventario = inv.codigo_inventario AND inv.comunidad = ?1 AND MONTH(ven.fecha) > 6 AND MONTH(ven.fecha) < 10 AND YEAR(ven.fecha) = ?2",nativeQuery=true)
    List<ReportesTotalDto> obtenerVentasDeComunidadDelTERCERTrimestre(String comunidadId, int anho);

    @Query(value="SELECT ven.* FROM mosqoy.Ventas ven, mosqoy.Inventario inv, mosqoy.Comunidades com WHERE ven.productoinventario = inv.codigo_inventario AND inv.comunidad = ?1 AND MONTH(ven.fecha) > 9 AND MONTH(ven.fecha) < 13 AND YEAR(ven.fecha) = ?2",nativeQuery=true)
    List<ReportesTotalDto> obtenerVentasDeComunidadDelCUARTOTrimestre(String comunidadId, int anho);

    /////////// FIN ventas de una comunidad por TRIMESTRE especifico en un anho especifico

    /////////// ventas de una comunidad por MES especifico en un anho especifico

    @Query(value="SELECT ven.* FROM mosqoy.Ventas ven, mosqoy.Inventario inv WHERE ven.productoinventario = inv.codigo_inventario AND inv.comunidad = ?1 AND MONTH(ven.fecha) = 1 AND YEAR(ven.fecha) = ?2",nativeQuery=true)
    List<ReportesTotalDto> obtenerVentasDeComunidadDeENERO(String comunidadId, int anho);

    @Query(value="SELECT ven.* FROM mosqoy.Ventas ven, mosqoy.Inventario inv WHERE ven.productoinventario = inv.codigo_inventario AND inv.comunidad = ?1 AND MONTH(ven.fecha) = 2 AND YEAR(ven.fecha) = ?2",nativeQuery=true)
    List<ReportesTotalDto> obtenerVentasDeComunidadDeFEBRERO(String comunidadId, int anho);

    @Query(value="SELECT ven.* FROM mosqoy.Ventas ven, mosqoy.Inventario inv WHERE ven.productoinventario = inv.codigo_inventario AND inv.comunidad = ?1 AND MONTH(ven.fecha) = 3 AND YEAR(ven.fecha) = ?2",nativeQuery=true)
    List<ReportesTotalDto> obtenerVentasDeComunidadDeMARZO(String comunidadId, int anho);

    @Query(value="SELECT ven.* FROM mosqoy.Ventas ven, mosqoy.Inventario inv WHERE ven.productoinventario = inv.codigo_inventario AND inv.comunidad = ?1 AND MONTH(ven.fecha) = 4 AND YEAR(ven.fecha) = ?2",nativeQuery=true)
    List<ReportesTotalDto> obtenerVentasDeComunidadDeABRIL(String comunidadId, int anho);

    @Query(value="SELECT ven.* FROM mosqoy.Ventas ven, mosqoy.Inventario inv WHERE ven.productoinventario = inv.codigo_inventario AND inv.comunidad = ?1 AND MONTH(ven.fecha) = 5 AND YEAR(ven.fecha) = ?2",nativeQuery=true)
    List<ReportesTotalDto> obtenerVentasDeComunidadDeMAYO(String comunidadId, int anho);

    @Query(value="SELECT ven.* FROM mosqoy.Ventas ven, mosqoy.Inventario inv WHERE ven.productoinventario = inv.codigo_inventario AND inv.comunidad = ?1 AND MONTH(ven.fecha) = 6 AND YEAR(ven.fecha) = ?2",nativeQuery=true)
    List<ReportesTotalDto> obtenerVentasDeComunidadDeJUNIO(String comunidadId, int anho);

    @Query(value="SELECT ven.* FROM mosqoy.Ventas ven, mosqoy.Inventario inv WHERE ven.productoinventario = inv.codigo_inventario AND inv.comunidad = ?1 AND MONTH(ven.fecha) = 7 AND YEAR(ven.fecha) = ?2",nativeQuery=true)
    List<ReportesTotalDto> obtenerVentasDeComunidadDeJULIO(String comunidadId, int anho);

    @Query(value="SELECT ven.* FROM mosqoy.Ventas ven, mosqoy.Inventario inv WHERE ven.productoinventario = inv.codigo_inventario AND inv.comunidad = ?1 AND MONTH(ven.fecha) = 8 AND YEAR(ven.fecha) = ?2",nativeQuery=true)
    List<ReportesTotalDto> obtenerVentasDeComunidadDeAGOSTO(String comunidadId, int anho);

    @Query(value="SELECT ven.* FROM mosqoy.Ventas ven, mosqoy.Inventario inv WHERE ven.productoinventario = inv.codigo_inventario AND inv.comunidad = ?1 AND MONTH(ven.fecha) = 9 AND YEAR(ven.fecha) = ?2",nativeQuery=true)
    List<ReportesTotalDto> obtenerVentasDeComunidadDeSETIEMBRE(String comunidadId, int anho);

    @Query(value="SELECT ven.* FROM mosqoy.Ventas ven, mosqoy.Inventario inv WHERE ven.productoinventario = inv.codigo_inventario AND inv.comunidad = ?1 AND MONTH(ven.fecha) = 10 AND YEAR(ven.fecha) = ?2",nativeQuery=true)
    List<ReportesTotalDto> obtenerVentasDeComunidadDeOCTUBRE(String comunidadId, int anho);

    @Query(value="SELECT ven.* FROM mosqoy.Ventas ven, mosqoy.Inventario inv WHERE ven.productoinventario = inv.codigo_inventario AND inv.comunidad = ?1 AND MONTH(ven.fecha) = 11 AND YEAR(ven.fecha) = ?2",nativeQuery=true)
    List<ReportesTotalDto> obtenerVentasDeComunidadDeNOVIEMBRE(String comunidadId, int anho);

    @Query(value="SELECT ven.* FROM mosqoy.Ventas ven, mosqoy.Inventario inv WHERE ven.productoinventario = inv.codigo_inventario AND inv.comunidad = ?1 AND MONTH(ven.fecha) = 12 AND YEAR(ven.fecha) = ?2",nativeQuery=true)
    List<ReportesTotalDto> obtenerVentasDeComunidadDeDICIEMBRE(String comunidadId, int anho);

    /////////// FIN ventas de una comunidad por MES especifico en un anho especifico



























    /////////// ventas de un producto por anho especifico

    @Query(value="SELECT ven.* FROM mosqoy.Ventas ven, mosqoy.Inventario inv WHERE ven.productoinventario = inv.codigo_inventario AND inv.producto = ?1 AND YEAR(ven.fecha) = ?2",nativeQuery=true)
    List<ReportesTotalDto> obtenerVentasDeProductoPorAnho(String productoId, int anho);

    /////////// FIN ventas de un producto por anho especifico

    /////////// ventas de una producto por TRIMESTRE especifico en un anho especifico

    @Query(value="SELECT ven.* FROM mosqoy.Ventas ven, mosqoy.Inventario inv WHERE ven.productoinventario = inv.codigo_inventario AND inv.producto = ?1 AND YEAR(ven.fecha) = ?2 AND MONTH(ven.fecha) > 0 AND MONTH(ven.fecha) < 4",nativeQuery=true)
    List<ReportesTotalDto> obtenerVentasDeProductoDelPRIMERTrimestre(String productoId, int anho);

    @Query(value="SELECT ven.* FROM mosqoy.Ventas ven, mosqoy.Inventario inv WHERE ven.productoinventario = inv.codigo_inventario AND inv.producto = ?1 AND YEAR(ven.fecha) = ?2 AND MONTH(ven.fecha) > 3 AND MONTH(ven.fecha) < 7",nativeQuery=true)
    List<ReportesTotalDto> obtenerVentasDeProductoDelSEGUNDOTrimestre(String productoId, int anho);

    @Query(value="SELECT ven.* FROM mosqoy.Ventas ven, mosqoy.Inventario inv WHERE ven.productoinventario = inv.codigo_inventario AND inv.producto = ?1 AND YEAR(ven.fecha) = ?2 AND MONTH(ven.fecha) > 6 AND MONTH(ven.fecha) < 10",nativeQuery=true)
    List<ReportesTotalDto> obtenerVentasDeProductoDelTERCERTrimestre(String productoId, int anho);

    @Query(value="SELECT ven.* FROM mosqoy.Ventas ven, mosqoy.Inventario inv WHERE ven.productoinventario = inv.codigo_inventario AND inv.producto = ?1 AND YEAR(ven.fecha) = ?2 AND MONTH(ven.fecha) > 9 AND MONTH(ven.fecha) < 13",nativeQuery=true)
    List<ReportesTotalDto> obtenerVentasDeProductoDelCUARTOTrimestre(String productoId, int anho);

    /////////// FIN ventas de un producto por TRIMESTRE especifico en un anho especifico

    /////////// ventas de un producto por MES especifico en un anho especifico

    @Query(value="SELECT ven.* FROM mosqoy.Ventas ven, mosqoy.Inventario inv WHERE ven.productoinventario = inv.codigo_inventario AND inv.producto = ?1 AND YEAR(ven.fecha) = ?2 AND MONTH(ven.fecha) = 1",nativeQuery=true)
    List<ReportesTotalDto> obtenerVentasDeProductoDeENERO(String productoId, int anho);

    @Query(value="SELECT ven.* FROM mosqoy.Ventas ven, mosqoy.Inventario inv WHERE ven.productoinventario = inv.codigo_inventario AND inv.producto = ?1 AND YEAR(ven.fecha) = ?2 AND MONTH(ven.fecha) = 2",nativeQuery=true)
    List<ReportesTotalDto> obtenerVentasDeProductoDeFEBRERO(String productoId, int anho);

    @Query(value="SELECT ven.* FROM mosqoy.Ventas ven, mosqoy.Inventario inv WHERE ven.productoinventario = inv.codigo_inventario AND inv.producto = ?1 AND YEAR(ven.fecha) = ?2 AND MONTH(ven.fecha) = 3",nativeQuery=true)
    List<ReportesTotalDto> obtenerVentasDeProductoDeMARZO(String productoId, int anho);

    @Query(value="SELECT ven.* FROM mosqoy.Ventas ven, mosqoy.Inventario inv WHERE ven.productoinventario = inv.codigo_inventario AND inv.producto = ?1 AND YEAR(ven.fecha) = ?2 AND MONTH(ven.fecha) = 4",nativeQuery=true)
    List<ReportesTotalDto> obtenerVentasDeProductoDeABRIL(String productoId, int anho);

    @Query(value="SELECT ven.* FROM mosqoy.Ventas ven, mosqoy.Inventario inv WHERE ven.productoinventario = inv.codigo_inventario AND inv.producto = ?1 AND YEAR(ven.fecha) = ?2 AND MONTH(ven.fecha) = 5",nativeQuery=true)
    List<ReportesTotalDto> obtenerVentasDeProductoDeMAYO(String productoId, int anho);

    @Query(value="SELECT ven.* FROM mosqoy.Ventas ven, mosqoy.Inventario inv WHERE ven.productoinventario = inv.codigo_inventario AND inv.producto = ?1 AND YEAR(ven.fecha) = ?2 AND MONTH(ven.fecha) = 6",nativeQuery=true)
    List<ReportesTotalDto> obtenerVentasDeProductoDeJUNIO(String productoId, int anho);

    @Query(value="SELECT ven.* FROM mosqoy.Ventas ven, mosqoy.Inventario inv WHERE ven.productoinventario = inv.codigo_inventario AND inv.producto = ?1 AND YEAR(ven.fecha) = ?2 AND MONTH(ven.fecha) = 7",nativeQuery=true)
    List<ReportesTotalDto> obtenerVentasDeProductoDeJULIO(String productoId, int anho);

    @Query(value="SELECT ven.* FROM mosqoy.Ventas ven, mosqoy.Inventario inv WHERE ven.productoinventario = inv.codigo_inventario AND inv.producto = ?1 AND YEAR(ven.fecha) = ?2 AND MONTH(ven.fecha) = 8",nativeQuery=true)
    List<ReportesTotalDto> obtenerVentasDeProductoDeAGOSTO(String productoId, int anho);

    @Query(value="SELECT ven.* FROM mosqoy.Ventas ven, mosqoy.Inventario inv WHERE ven.productoinventario = inv.codigo_inventario AND inv.producto = ?1 AND YEAR(ven.fecha) = ?2 AND MONTH(ven.fecha) = 9",nativeQuery=true)
    List<ReportesTotalDto> obtenerVentasDeProductoDeSETIEMBRE(String productoId, int anho);

    @Query(value="SELECT ven.* FROM mosqoy.Ventas ven, mosqoy.Inventario inv WHERE ven.productoinventario = inv.codigo_inventario AND inv.producto = ?1 AND YEAR(ven.fecha) = ?2 AND MONTH(ven.fecha) = 10",nativeQuery=true)
    List<ReportesTotalDto> obtenerVentasDeProductoDeOCTUBRE(String productoId, int anho);

    @Query(value="SELECT ven.* FROM mosqoy.Ventas ven, mosqoy.Inventario inv WHERE ven.productoinventario = inv.codigo_inventario AND inv.producto = ?1 AND YEAR(ven.fecha) = ?2 AND MONTH(ven.fecha) = 11",nativeQuery=true)
    List<ReportesTotalDto> obtenerVentasDeProductoDeNOVIEMBRE(String productoId, int anho);

    @Query(value="SELECT ven.* FROM mosqoy.Ventas ven, mosqoy.Inventario inv WHERE ven.productoinventario = inv.codigo_inventario AND inv.producto = ?1 AND YEAR(ven.fecha) = ?2 AND MONTH(ven.fecha) = 12",nativeQuery=true)
    List<ReportesTotalDto> obtenerVentasDeProductoDeDICIEMBRE(String productoId, int anho);

    /////////// FIN ventas de un producto por MES especifico en un anho especifico












    //SEDES ALEX

    @Query(value="SELECT CONCAT(u.nombre,' ',u.apellido) as nombre, u.dni, u.correo, u.telefono, sum(v.precio_venta*v.cantidad) as sumaventas, sum(v.cantidad) as cantidadvendidos FROM Ventas v inner join Usuarios u ON (v.vendedor = u.dni) WHERE YEAR(v.fecha) = ?1 AND u.rol = 3 AND v.confirmado = 1 group by v.vendedor",nativeQuery=true)
    List<ReportesSedesDto> obtenerReporteAnualSede(int anho);

    @Query(value="SELECT CONCAT(u.nombre,' ',u.apellido) as nombre, u.dni, u.correo, u.telefono, sum(v.precio_venta*v.cantidad) as sumaventas, sum(v.cantidad) as cantidadvendidos FROM Ventas v inner join Usuarios u ON (v.vendedor = u.dni) WHERE QUARTER(v.fecha) = ?1 AND YEAR(v.fecha) = ?2 AND u.rol = 3 AND v.confirmado = 1 group by v.vendedor",nativeQuery=true)
    List<ReportesSedesDto> obtenerReporteTrimestralSede(int trimestre, int anho);

    @Query(value="SELECT CONCAT(u.nombre,' ',u.apellido) as nombre, u.dni, u.correo, u.telefono, sum(v.precio_venta*v.cantidad) as sumaventas, sum(v.cantidad) as cantidadvendidos FROM Ventas v inner join Usuarios u ON (v.vendedor = u.dni) WHERE MONTH(v.fecha) = ?1 AND YEAR(v.fecha) = ?2 AND u.rol = 3 AND v.confirmado = 1 group by v.vendedor ",nativeQuery=true)
    List<ReportesSedesDto> obtenerReporteMensualSede(int mes, int anho);

    //FIN SEDES ALEX

    //CLIENTES ALEX

    @Query(value="SELECT v.nombrecliente as nombre , v.ruc_dni, v.productoinventario as producto, sum(v.precio_venta*v.cantidad) as sumaventas, sum(v.cantidad) as cantidadvendidos FROM Ventas v WHERE YEAR(v.fecha) = ?1 AND v.confirmado = 1 group by v.ruc_dni",nativeQuery=true)
    List<ReportesClienteDto> obtenerReporteAnualCliente(int anho);

    @Query(value="SELECT v.nombrecliente as nombre , v.ruc_dni, v.productoinventario as producto, sum(v.precio_venta*v.cantidad) as sumaventas, sum(v.cantidad) as cantidadvendidos FROM Ventas v WHERE QUARTER(v.fecha) = ?1 AND v.confirmado = 1 AND YEAR(v.fecha) = ?2 group by v.ruc_dni",nativeQuery=true)
    List<ReportesClienteDto> obtenerReporteTrimestralCliente(int trimestre, int anho);

    @Query(value="SELECT v.nombrecliente as nombre , v.ruc_dni, v.productoinventario as producto, sum(v.precio_venta*v.cantidad) as sumaventas, sum(v.cantidad) as cantidadvendidos FROM Ventas v WHERE MONTH(v.fecha) = ?1 AND v.confirmado = 1 AND YEAR(v.fecha) = ?2 group by v.ruc_dni",nativeQuery=true)
    List<ReportesClienteDto> obtenerReporteMensualCliente(int mes, int anho);

    // FIN CLIENTES ALEX

    //COMUNIDAD FER

    @Query(value="SELECT comu.codigo, comu.nombre, artesanos.cantidadartesanos, cantidades.cantidadvendidos, cantidades.sumaventas FROM mosqoy.Comunidades comu, (SELECT com.codigo, SUM(ven.cantidad) AS \"cantidadvendidos\", SUM(ven.precio_venta*ven.cantidad) AS \"sumaventas\" FROM mosqoy.Ventas ven, mosqoy.Comunidades com, mosqoy.Inventario inv WHERE YEAR(ven.fecha) = ?1 AND ven.productoinventario = inv.codigo_inventario AND inv.comunidad = com.codigo GROUP BY com.codigo) cantidades, (SELECT com.codigo, COUNT(art.codigo) AS \"cantidadartesanos\" FROM mosqoy.Comunidades com, mosqoy.Artesanos art WHERE com.codigo = art.comunidad GROUP BY com.codigo) artesanos WHERE comu.codigo = cantidades.codigo AND comu.codigo = artesanos.codigo",nativeQuery=true)
    List<ReportesComunidadDto> obtenerReporteAnualComunidad(int anho);

    @Query(value="SELECT comu.codigo, comu.nombre, artesanos.cantidadartesanos, cantidades.cantidadvendidos, cantidades.sumaventas FROM mosqoy.Comunidades comu, (SELECT com.codigo, SUM(ven.cantidad) AS \"cantidadvendidos\", SUM(ven.precio_venta*ven.cantidad) AS \"sumaventas\" FROM mosqoy.Ventas ven, mosqoy.Comunidades com, mosqoy.Inventario inv WHERE QUARTER(ven.fecha) = ?1 AND YEAR(ven.fecha) = ?2 AND ven.productoinventario = inv.codigo_inventario AND inv.comunidad = com.codigo GROUP BY com.codigo) cantidades, (SELECT com.codigo, COUNT(art.codigo) AS \"cantidadartesanos\" FROM mosqoy.Comunidades com, mosqoy.Artesanos art WHERE com.codigo = art.comunidad GROUP BY com.codigo) artesanos WHERE comu.codigo = cantidades.codigo AND comu.codigo = artesanos.codigo",nativeQuery=true)
    List<ReportesComunidadDto> obtenerReporteTrimestralComunidad(int trimestre, int anho);

    @Query(value="SELECT comu.codigo, comu.nombre, artesanos.cantidadartesanos, cantidades.cantidadvendidos, cantidades.sumaventas FROM mosqoy.Comunidades comu, (SELECT com.codigo, SUM(ven.cantidad) AS \"cantidadvendidos\", SUM(ven.precio_venta*ven.cantidad) AS \"sumaventas\" FROM mosqoy.Ventas ven, mosqoy.Comunidades com, mosqoy.Inventario inv WHERE MONTH(ven.fecha) = ?1 AND YEAR(ven.fecha) = ?2 AND ven.productoinventario = inv.codigo_inventario AND inv.comunidad = com.codigo GROUP BY com.codigo) cantidades, (SELECT com.codigo, COUNT(art.codigo) AS \"cantidadartesanos\" FROM mosqoy.Comunidades com, mosqoy.Artesanos art WHERE com.codigo = art.comunidad GROUP BY com.codigo) artesanos WHERE comu.codigo = cantidades.codigo AND comu.codigo = artesanos.codigo",nativeQuery=true)
    List<ReportesComunidadDto> obtenerReporteMensualComunidad(int mes, int anho);

    //FIN COMUNIDAD FER

    //ARTICULOS FER

    @Query(value="SELECT pro.codigonom, pro.linea, pro.nombre, sumaven.sumaventas, cantven.cantidadvendidos FROM mosqoy.Productos pro, (SELECT pro.codigonom, pro.linea, SUM(ven.precio_venta*ven.cantidad) AS \"sumaventas\" FROM mosqoy.Ventas ven, mosqoy.Productos pro, mosqoy.Inventario inv WHERE ven.productoinventario = inv.codigo_inventario AND inv.producto = pro.codigonom AND inv.linea = pro.linea AND YEAR(ven.fecha) = ?1 GROUP BY pro.linea, pro.codigonom) sumaven, (SELECT pro.codigonom, pro.linea, SUM(ven.cantidad) AS \"cantidadvendidos\" FROM mosqoy.Ventas ven, mosqoy.Productos pro, mosqoy.Inventario inv WHERE ven.productoinventario = inv.codigo_inventario AND inv.producto = pro.codigonom AND inv.linea = pro.linea GROUP BY pro.linea, pro.codigonom) cantven WHERE pro.linea = sumaven.linea AND pro.codigonom = sumaven.codigonom AND pro.linea = cantven.linea AND pro.codigonom = cantven.codigonom",nativeQuery=true)
    List<ReportesArticuloDto> obtenerReporteAnualArticuloProducto(int anho);

    @Query(value="SELECT pro.codigonom, pro.linea, pro.nombre, sumaven.sumaventas, cantven.cantidadvendidos FROM mosqoy.Productos pro, (SELECT pro.codigonom, pro.linea, SUM(ven.precio_venta*ven.cantidad) AS \"sumaventas\" FROM mosqoy.Ventas ven, mosqoy.Productos pro, mosqoy.Inventario inv WHERE ven.productoinventario = inv.codigo_inventario AND inv.producto = pro.codigonom AND inv.linea = pro.linea AND QUARTER(ven.fecha) = ?1 AND YEAR(ven.fecha) = ?2 GROUP BY pro.linea, pro.codigonom) sumaven, (SELECT pro.codigonom, pro.linea, SUM(ven.cantidad) AS \"cantidadvendidos\" FROM mosqoy.Ventas ven, mosqoy.Productos pro, mosqoy.Inventario inv WHERE ven.productoinventario = inv.codigo_inventario AND inv.producto = pro.codigonom AND inv.linea = pro.linea GROUP BY pro.linea, pro.codigonom) cantven WHERE pro.linea = sumaven.linea AND pro.codigonom = sumaven.codigonom AND pro.linea = cantven.linea AND pro.codigonom = cantven.codigonom",nativeQuery=true)
    List<ReportesArticuloDto> obtenerReporteTrimestralArticuloProducto(int trimestre, int  anho);

    @Query(value="SELECT pro.codigonom, pro.linea, pro.nombre, sumaven.sumaventas, cantven.cantidadvendidos FROM mosqoy.Productos pro, (SELECT pro.codigonom, pro.linea, SUM(ven.precio_venta*ven.cantidad) AS \"sumaventas\" FROM mosqoy.Ventas ven, mosqoy.Productos pro, mosqoy.Inventario inv WHERE ven.productoinventario = inv.codigo_inventario AND inv.producto = pro.codigonom AND inv.linea = pro.linea AND MONTH(ven.fecha) = ?1 AND YEAR(ven.fecha) = ?2 GROUP BY pro.linea, pro.codigonom) sumaven, (SELECT pro.codigonom, pro.linea, SUM(ven.cantidad) AS \"cantidadvendidos\" FROM mosqoy.Ventas ven, mosqoy.Productos pro, mosqoy.Inventario inv WHERE ven.productoinventario = inv.codigo_inventario AND inv.producto = pro.codigonom AND inv.linea = pro.linea GROUP BY pro.linea, pro.codigonom) cantven WHERE pro.linea = sumaven.linea AND pro.codigonom = sumaven.codigonom AND pro.linea = cantven.linea AND pro.codigonom = cantven.codigonom",nativeQuery=true)
    List<ReportesArticuloDto> obtenerReporteMensualArticuloProducto(int mes, int anho);

    //FIN ARTICULOS FER

    //TOTAL FER

    @Query(value="SELECT ven.ruc_dni, ven.nombrecliente, ven.mediopago, ven.tipodocumento, ven.numerodocumento, ven.precio_venta , ven.lugarventa, ven.productoinventario, ven.fecha, ven.cantidad, CONCAT(usu.nombre, \" \",usu.apellido) AS \"vendedor\", usu.dni AS \"dnivendedor\", ven.media FROM mosqoy.Ventas ven, mosqoy.Usuarios usu WHERE YEAR(ven.fecha) = ?1 AND ven.confirmado = 1 AND ven.vendedor = usu.dni",nativeQuery=true)
    List<ReportesTotalDto> obtenerReporteAnualTotal(int anho);

    @Query(value="SELECT ven.ruc_dni, ven.nombrecliente, ven.mediopago, ven.tipodocumento, ven.numerodocumento, ven.precio_venta , ven.lugarventa, ven.productoinventario, ven.fecha, ven.cantidad, CONCAT(usu.nombre, \" \",usu.apellido) AS \"vendedor\", usu.dni AS \"dnivendedor\", ven.media FROM mosqoy.Ventas ven, mosqoy.Usuarios usu WHERE QUARTER(ven.fecha) = ?1 AND ven.confirmado = 1 AND YEAR(ven.fecha) = ?2 AND ven.vendedor = usu.dni",nativeQuery=true)
    List<ReportesTotalDto> obtenerReporteTrimestralTotal(int trimestre, int anho);

    @Query(value="SELECT ven.ruc_dni, ven.nombrecliente, ven.mediopago, ven.tipodocumento, ven.numerodocumento, ven.precio_venta , ven.lugarventa, ven.productoinventario, ven.fecha, ven.cantidad, CONCAT(usu.nombre, \" \",usu.apellido) AS \"vendedor\", usu.dni AS \"dnivendedor\", ven.media FROM mosqoy.Ventas ven, mosqoy.Usuarios usu WHERE MONTH(ven.fecha) = ?1 AND ven.confirmado = 1 AND YEAR(ven.fecha) = ?2 AND ven.vendedor = usu.dni",nativeQuery=true)
    List<ReportesTotalDto> obtenerReporteMensualTotal(int mes, int anho);

    // FIN TOTAL FER







    /*----------------------------------- QUERIES PARA SEDES -----------------------------------*/

    //TOTAL ALEX

    @Query(value="SELECT ven.ruc_dni, ven.nombrecliente, ven.mediopago, ven.tipodocumento, ven.precio_venta, ven.numerodocumento, ven.lugarventa, ven.productoinventario, ven.fecha, ven.cantidad, CONCAT(usu.nombre, \" \",usu.apellido) AS \"vendedor\", usu.dni AS \"dnivendedor\" FROM mosqoy.Ventas ven, mosqoy.Usuarios usu WHERE YEAR(ven.fecha) = ?1 AND ven.confirmado = 1 AND ven.vendedor = usu.dni AND usu.dni = ?2 ",nativeQuery=true)
    List<ReportesTotalDto> obtenerReporteSedeAnualTotal(int anho, int idusuario);

    @Query(value="SELECT ven.ruc_dni, ven.nombrecliente, ven.mediopago, ven.tipodocumento, ven.precio_venta, ven.numerodocumento, ven.lugarventa, ven.productoinventario, ven.fecha, ven.cantidad, CONCAT(usu.nombre, \" \",usu.apellido) AS \"vendedor\", usu.dni AS \"dnivendedor\" FROM mosqoy.Ventas ven, mosqoy.Usuarios usu WHERE QUARTER(ven.fecha) = ?1 AND YEAR(ven.fecha) = ?2 AND ven.confirmado = 1 AND ven.vendedor = usu.dni AND usu.dni = ?3",nativeQuery=true)
    List<ReportesTotalDto> obtenerReporteSedeTrimestralTotal(int trimestre, int anho, int idusuario);

    @Query(value="SELECT ven.ruc_dni, ven.nombrecliente, ven.mediopago, ven.tipodocumento, ven.precio_venta, ven.numerodocumento, ven.lugarventa, ven.productoinventario, ven.fecha, ven.cantidad, CONCAT(usu.nombre, \" \",usu.apellido) AS \"vendedor\", usu.dni AS \"dnivendedor\" FROM mosqoy.Ventas ven, mosqoy.Usuarios usu WHERE MONTH(ven.fecha) = ?1 AND YEAR(ven.fecha) = ?2 AND ven.confirmado = 1 AND ven.vendedor = usu.dni AND usu.dni = ?3",nativeQuery=true)
    List<ReportesTotalDto> obtenerReporteSedeMensualTotal(int mes, int anho, int idusuario);

    // FIN TOTAL ALEX

    //COMUNIDAD FER

    @Query(value="SELECT comu.codigo, comu.nombre, artesanos.cantidadartesanos, cantidades.cantidadvendidos, cantidades.sumaventas FROM mosqoy.Comunidades comu, (SELECT com.codigo, SUM(ven.cantidad) AS \"cantidadvendidos\", SUM(ven.precio_venta) AS \"sumaventas\" FROM mosqoy.Ventas ven, mosqoy.Comunidades com, mosqoy.Inventario inv WHERE YEAR(ven.fecha) = ?1 AND ven.vendedor = ?2 AND ven.productoinventario = inv.codigo_inventario AND inv.comunidad = com.codigo GROUP BY com.codigo) cantidades, (SELECT com.codigo, COUNT(art.codigo) AS \"cantidadartesanos\" FROM mosqoy.Comunidades com, mosqoy.Artesanos art WHERE com.codigo = art.comunidad GROUP BY com.codigo) artesanos WHERE comu.codigo = cantidades.codigo AND comu.codigo = artesanos.codigo",nativeQuery=true)
    List<ReportesComunidadDto> obtenerReporteSedeAnualComunidad(int anho, int idusuario);

    @Query(value="SELECT comu.codigo, comu.nombre, artesanos.cantidadartesanos, cantidades.cantidadvendidos, cantidades.sumaventas FROM mosqoy.Comunidades comu, (SELECT com.codigo, SUM(ven.cantidad) AS \"cantidadvendidos\", SUM(ven.precio_venta) AS \"sumaventas\" FROM mosqoy.Ventas ven, mosqoy.Comunidades com, mosqoy.Inventario inv WHERE QUARTER(ven.fecha) = ?1 AND YEAR(ven.fecha) = ?2 AND ven.vendedor = ?3 AND ven.productoinventario = inv.codigo_inventario AND inv.comunidad = com.codigo GROUP BY com.codigo) cantidades, (SELECT com.codigo, COUNT(art.codigo) AS \"cantidadartesanos\" FROM mosqoy.Comunidades com, mosqoy.Artesanos art WHERE com.codigo = art.comunidad GROUP BY com.codigo) artesanos WHERE comu.codigo = cantidades.codigo AND comu.codigo = artesanos.codigo",nativeQuery=true)
    List<ReportesComunidadDto> obtenerReporteSedeTrimestralComunidad(int trimestre, int anho, int idusuario);

    @Query(value="SELECT comu.codigo, comu.nombre, artesanos.cantidadartesanos, cantidades.cantidadvendidos, cantidades.sumaventas FROM mosqoy.Comunidades comu, (SELECT com.codigo, SUM(ven.cantidad) AS \"cantidadvendidos\", SUM(ven.precio_venta) AS \"sumaventas\" FROM mosqoy.Ventas ven, mosqoy.Comunidades com, mosqoy.Inventario inv WHERE MONTH(ven.fecha) = ?1 AND YEAR(ven.fecha) = ?2 AND ven.vendedor = ?3 AND ven.productoinventario = inv.codigo_inventario AND inv.comunidad = com.codigo GROUP BY com.codigo) cantidades, (SELECT com.codigo, COUNT(art.codigo) AS \"cantidadartesanos\" FROM mosqoy.Comunidades com, mosqoy.Artesanos art WHERE com.codigo = art.comunidad GROUP BY com.codigo) artesanos WHERE comu.codigo = cantidades.codigo AND comu.codigo = artesanos.codigo",nativeQuery=true)
    List<ReportesComunidadDto> obtenerReporteSedeMensualComunidad(int mes, int anho, int idusuario);

    //FIN COMUNIDAD FER


    //CLIENTES

    @Query(value="SELECT v.nombrecliente as nombre , v.ruc_dni, v.productoinventario as producto, sum(v.precio_venta) as sumaventas, sum(v.cantidad) as cantidadvendidos FROM Ventas v WHERE YEAR(v.fecha) = ?1 AND v.Vendedor =?2 group by v.ruc_dni",nativeQuery=true)
    List<ReportesClienteDto> obtenerReporteSedeAnualCliente(int anho, int idusuario);

    @Query(value="SELECT v.nombrecliente as nombre , v.ruc_dni, v.productoinventario as producto, sum(v.precio_venta) as sumaventas, sum(v.cantidad) as cantidadvendidos FROM Ventas v WHERE QUARTER(v.fecha) = ?1 AND YEAR(v.fecha) = ?2 AND v.Vendedor =?3 group by v.ruc_dni",nativeQuery=true)
    List<ReportesClienteDto> obtenerReporteSedeTrimestralCliente(int trimestre, int anho, int idusuario);

    @Query(value="SELECT v.nombrecliente as nombre , v.ruc_dni, v.productoinventario as producto, sum(v.precio_venta) as sumaventas, sum(v.cantidad) as cantidadvendidos FROM Ventas v WHERE MONTH(v.fecha) = ?1 AND YEAR(v.fecha) = ?2 AND v.Vendedor =?3 group by v.ruc_dni",nativeQuery=true)
    List<ReportesClienteDto> obtenerReporteSedeMensualCliente(int mes, int anho, int idusuario);

    // FIN CLIENTES

    //ARTICULOS FER

    @Query(value="SELECT pro.codigonom, pro.linea, pro.nombre, sumaven.sumaventas, cantven.cantidadvendidos FROM mosqoy.Productos pro, (SELECT pro.codigonom, pro.linea, SUM(ven.precio_venta) AS \"sumaventas\" FROM mosqoy.Ventas ven, mosqoy.Productos pro, mosqoy.Inventario inv WHERE ven.productoinventario = inv.codigo_inventario AND inv.producto = pro.codigonom AND inv.linea = pro.linea AND YEAR(ven.fecha) = ?1 AND ven.vendedor = ?2 GROUP BY pro.linea, pro.codigonom) sumaven, (SELECT pro.codigonom, pro.linea, SUM(ven.cantidad) AS \"cantidadvendidos\" FROM mosqoy.Ventas ven, mosqoy.Productos pro, mosqoy.Inventario inv WHERE ven.productoinventario = inv.codigo_inventario AND inv.producto = pro.codigonom AND inv.linea = pro.linea GROUP BY pro.linea, pro.codigonom) cantven WHERE pro.linea = sumaven.linea AND pro.codigonom = sumaven.codigonom AND pro.linea = cantven.linea AND pro.codigonom = cantven.codigonom",nativeQuery=true)
    List<ReportesArticuloDto> obtenerReporteSedeAnualArticuloProducto(int anho, int idusuario);

    @Query(value="SELECT pro.codigonom, pro.linea, pro.nombre, sumaven.sumaventas, cantven.cantidadvendidos FROM mosqoy.Productos pro, (SELECT pro.codigonom, pro.linea, SUM(ven.precio_venta) AS \"sumaventas\" FROM mosqoy.Ventas ven, mosqoy.Productos pro, mosqoy.Inventario inv WHERE ven.productoinventario = inv.codigo_inventario AND inv.producto = pro.codigonom AND inv.linea = pro.linea AND QUARTER(ven.fecha) = ?1 AND YEAR(ven.fecha) = ?2 AND ven.vendedor = ?3 GROUP BY pro.linea, pro.codigonom) sumaven, (SELECT pro.codigonom, pro.linea, SUM(ven.cantidad) AS \"cantidadvendidos\" FROM mosqoy.Ventas ven, mosqoy.Productos pro, mosqoy.Inventario inv WHERE ven.productoinventario = inv.codigo_inventario AND inv.producto = pro.codigonom AND inv.linea = pro.linea GROUP BY pro.linea, pro.codigonom) cantven WHERE pro.linea = sumaven.linea AND pro.codigonom = sumaven.codigonom AND pro.linea = cantven.linea AND pro.codigonom = cantven.codigonom",nativeQuery=true)
    List<ReportesArticuloDto> obtenerReporteSedeTrimestralArticuloProducto(int trimestre, int anho, int idusuario);

    @Query(value="SELECT pro.codigonom, pro.linea, pro.nombre, sumaven.sumaventas, cantven.cantidadvendidos FROM mosqoy.Productos pro, (SELECT pro.codigonom, pro.linea, SUM(ven.precio_venta) AS \"sumaventas\" FROM mosqoy.Ventas ven, mosqoy.Productos pro, mosqoy.Inventario inv WHERE ven.productoinventario = inv.codigo_inventario AND inv.producto = pro.codigonom AND inv.linea = pro.linea AND MONTH(ven.fecha) = ?1 AND YEAR(ven.fecha) = ?2 AND ven.vendedor = ?3 GROUP BY pro.linea, pro.codigonom) sumaven, (SELECT pro.codigonom, pro.linea, SUM(ven.cantidad) AS \"cantidadvendidos\" FROM mosqoy.Ventas ven, mosqoy.Productos pro, mosqoy.Inventario inv WHERE ven.productoinventario = inv.codigo_inventario AND inv.producto = pro.codigonom AND inv.linea = pro.linea GROUP BY pro.linea, pro.codigonom) cantven WHERE pro.linea = sumaven.linea AND pro.codigonom = sumaven.codigonom AND pro.linea = cantven.linea AND pro.codigonom = cantven.codigonom",nativeQuery=true)
    List<ReportesArticuloDto> obtenerReporteSedeMensualArticuloProducto(int mes, int anho, int idusuario);

    //FIN ARTICULOS FER


}