package com.example.sw2.service;

import com.example.sw2.constantes.CustomConstants;
import com.example.sw2.dtoReportes.ReportesArticuloDto;
import com.example.sw2.dtoReportes.ReportesClienteDto;
import com.example.sw2.dtoReportes.ReportesComunidadDto;
import com.example.sw2.dtoReportes.ReportesTotalDto;
import com.example.sw2.dtoReportes.ReportesSedesDto;
import com.example.sw2.entity.Reportes;
import com.example.sw2.repository.VentasRepository;
import com.example.sw2.utils.ReportesUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Service
public class ReporteGestorService extends ReportesUtils implements IReporteGestorService {

    @Autowired
    VentasRepository ventasRepository;

    @Override
    public ByteArrayInputStream generarReporte(Reportes reportes) throws Exception{

        Workbook workbook = new HSSFWorkbook();

        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        switch (reportes.getOrderBy()){
            case 1: //nos referimos al total
                llenarReporteTotal(workbook, reportes);

                break;
            case 2: //nos referimos a la sede
                llenarReporteSede(workbook, reportes);

                break;
            case 3: //nos referimos al articulo(producto)
                llenarReporteProducto(workbook, reportes);

                break;
            case 4: //nos referimos a la comunidad
                llenarReporteComunidad( workbook, reportes);
                break;
            case 5: //nos referimos al cliente
                llenarReporteCliente(workbook, reportes);

                break;

        }

        workbook.write(stream);
        workbook.close();

        return new ByteArrayInputStream(stream.toByteArray());

    }

    private void llenarReporteTotal(Workbook workbook, Reportes reportes){

        String[] columns = {"Documento","Doc. Número","Medio de Pago","Producto","Cliente","RUC","DNI","Vendedor","DNI vendedor","Precio Unit","Cantidad","Precio Total","Fecha de Venta","URL"};

        Sheet sheet= workbook.createSheet("Reporte total " + LocalDate.now().toString());
        setColumnWidths(sheet,reportes.getOrderBy());
        String titulo = "";
        List<ReportesTotalDto> reportesTotales = new ArrayList<>();
        switch (reportes.getType()){
            case 1:
                reportesTotales = ventasRepository.obtenerReporteAnualTotal(reportes.getYear());
                titulo = "Reporte total del año " + reportes.getYear();
                System.out.println("TestTest");
                System.out.println(titulo);
                break;
            case 2:
                reportesTotales = ventasRepository.obtenerReporteTrimestralTotal(reportes.getSelected(),reportes.getYear());
                titulo = "Reporte total del año " + reportes.getYear()+" trimestre " + CustomConstants.getTrimestre().get(reportes.getSelected());
                System.out.println(titulo);
                break;
            case 3:
                reportesTotales = ventasRepository.obtenerReporteMensualTotal(reportes.getSelected(),reportes.getYear());
                titulo = "Reporte total del año " + reportes.getYear()+" mes " + CustomConstants.getMeses().get(reportes.getSelected());
                System.out.println(titulo);
                break;
        }
        fillCellsInSheet(sheet,columns,reportesTotales,workbook,titulo);
    }

    private void llenarReporteSede(Workbook workbook, Reportes reportes){
        String[] columns = {"Nombre","DNI","Correo","Telefono","Precio Total","Cantidad Productos Vendidos"};
        Sheet sheet= workbook.createSheet("Reporte de sede " + LocalDate.now().toString());
        setColumnWidths(sheet,reportes.getOrderBy());
        String titulo = "";
        List<ReportesSedesDto> reportesSedes = new ArrayList<>();
        switch (reportes.getType()){
            case 1:
                reportesSedes = ventasRepository.obtenerReporteAnualSede(reportes.getYear());
                titulo = "Reporte total por sede del año " + reportes.getYear();
                break;
            case 2:
                reportesSedes = ventasRepository.obtenerReporteTrimestralSede(reportes.getSelected(),reportes.getYear());
                titulo = "Reporte total por sede del año " + reportes.getYear()+" trimestre " + CustomConstants.getTrimestre().get(reportes.getSelected());
                break;
            case 3:
                reportesSedes = ventasRepository.obtenerReporteMensualSede(reportes.getSelected(),reportes.getYear());
                titulo = "Reporte total por sede del año del " + reportes.getYear()+" mes " + CustomConstants.getMeses().get(reportes.getSelected());
                break;
        }
        fillCellsInSheet(sheet,columns,reportesSedes,workbook,titulo);
    }
    private void llenarReporteProducto(Workbook workbook, Reportes reportes){
        String[] columns = {"Nombre","Linea","Codigo","Precio Total","Cantidad Vendidos"};
        Sheet sheet= workbook.createSheet("Reporte producto " + LocalDate.now().toString());
        setColumnWidths(sheet,reportes.getOrderBy());
        String titulo = "";
        List<ReportesArticuloDto> reportesArticulos = new ArrayList<>();
        switch (reportes.getType()){
            case 1:
                reportesArticulos = ventasRepository.obtenerReporteAnualArticuloProducto(reportes.getYear());
                titulo = "Ventas por Artículos del año " + reportes.getYear();
                break;
            case 2:
                reportesArticulos = ventasRepository.obtenerReporteTrimestralArticuloProducto(reportes.getSelected(),reportes.getYear());
                titulo = "Ventas por Artículos del año " + reportes.getYear()+" trimestre " + CustomConstants.getTrimestre().get(reportes.getSelected());
                break;
            case 3:
                reportesArticulos = ventasRepository.obtenerReporteMensualArticuloProducto(reportes.getSelected(),reportes.getYear());
                titulo = "Ventas por Artículos del año " + reportes.getYear()+" mes " + CustomConstants.getMeses().get(reportes.getSelected());
                break;
        }
        fillCellsInSheet(sheet,columns,reportesArticulos,workbook,titulo);
    }
    private void llenarReporteComunidad(Workbook workbook, Reportes reportes){
        String[] columns = {"Nombre","Código","Cantidad Artesanos","Precio Total","Cantidad Productos Vendidos"};
        Sheet sheet= workbook.createSheet("Reporte comunidad " + LocalDate.now().toString());
        setColumnWidths(sheet,reportes.getOrderBy());
        String titulo = "";
        List<ReportesComunidadDto> reportesComunidad = new ArrayList<>();
        switch (reportes.getType()){
            case 1:
                reportesComunidad = ventasRepository.obtenerReporteAnualComunidad(reportes.getYear());
                titulo = "Ventas por Comunidad del año " + reportes.getYear();
                break;
            case 2:
                reportesComunidad = ventasRepository.obtenerReporteTrimestralComunidad(reportes.getSelected(),reportes.getYear());
                titulo = "Ventas por Comunidad del año " + reportes.getYear()+" trimestre " + CustomConstants.getTrimestre().get(reportes.getSelected());
                break;
            case 3:
                reportesComunidad = ventasRepository.obtenerReporteMensualComunidad(reportes.getSelected(),reportes.getYear());
                titulo = "Ventas por Comunidad del año " + reportes.getYear()+" mes " + CustomConstants.getMeses().get(reportes.getSelected());
                break;
        }
        fillCellsInSheet(sheet,columns,reportesComunidad,workbook,titulo);
    }
    private void llenarReporteCliente(Workbook workbook, Reportes reportes){
        String[] columns = {"Nombre","RUC","DNI","Producto más comprado","Suma de Ventas","Cantidad Vendida"};
        Sheet sheet= workbook.createSheet("Reporte de clientes " + LocalDate.now().toString());
        setColumnWidths(sheet,reportes.getOrderBy());
        String titulo = "";
        List<ReportesClienteDto> reportesClientes = new ArrayList<>();
        switch (reportes.getType()){
            case 1:
                reportesClientes = ventasRepository.obtenerReporteAnualCliente(reportes.getYear());
                titulo = "Reporte total por cliente del año " + reportes.getYear();
                break;
            case 2:
                reportesClientes = ventasRepository.obtenerReporteTrimestralCliente(reportes.getSelected(),reportes.getYear());
                titulo = "Reporte total por cliente del año " + reportes.getYear()+" trimestre " + CustomConstants.getTrimestre().get(reportes.getSelected());
                break;
            case 3:
                reportesClientes = ventasRepository.obtenerReporteMensualCliente(reportes.getSelected(),reportes.getYear());
                titulo = "Reporte total por cliente del año " + reportes.getYear()+" mes " + CustomConstants.getMeses().get(reportes.getSelected());
                break;
        }
        fillCellsInSheet(sheet,columns,reportesClientes,workbook,titulo);
    }

}
