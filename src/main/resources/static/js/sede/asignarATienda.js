var contextPath = window.location.href;

$(document).on("click", ".asign-A-Tienda", function () {

    $("#AsignarProductoModal  input").val('');


    $("#AsignarProductoModal  #idgestor2").val($(this).data('id1'));
    $("#AsignarProductoModal  #idsede2").val($(this).data('id2'));
    $("#AsignarProductoModal  #idproductoinventario2").val($(this).data('id3'));
    $("#AsignarProductoModal  #idestadoasign2").val($(this).data('id4'));
    $("#AsignarProductoModal  #idprecioventa2").val($(this).data('id5'));
    $("#AsignarProductoModal  #idtienda").val('0');
    $("#AsignarProductoModal  #fechaasignacion").val('');
    $("#AsignarProductoModal  #stock").val('');
    $("#AsignarProductoModal  #tienda").val('');



});


