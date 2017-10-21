$(document).ready(function() {

  console.log("document ready called...");

  $.ajax( {
    url: "freecom/apis/funds/all-funds",
    dataType: "json",
    /*data: {
      term: request.term
    },*/
    success: function( data ) {
      var result = [];
      for (var elem in data) {
        result.push({'value': data[elem]['schemeCode'], 'label': data[elem]['name']});
      }
      // response( result );

      $("#mfchoser").autocomplete({
           source: result,
           minLength: 2,
           select: function( event, ui ) {
             event.preventDefault();
             $("#mfchoser").val(ui.item.label);
             console.log( "Selected: " + ui.item.value + " aka " + ui.item.label );

             $("#portfolio-grid").jqGrid('setGridParam',{url:"freecom/apis/funds/fund-portfolio?schemecode=" + ui.item.value});
             $("#portfolio-grid").jqGrid('setCaption',"Portforio Details of " + ui.item.label)
             				.trigger('reloadGrid');
           },
           focus: function(event, ui) {
             event.preventDefault();
             $("#mfchoser").val(ui.item.label);
           }
         } );
    }
  } );

  jQuery("#portfolio-grid").jqGrid({
  	height: 600,
    url:'',
  	datatype: "json",
     	colNames:['Instrument Name', 'Percent'],
     	colModel:[
     		{name:'name',index:'name', width:300},
     		{name:'percent', index:'percent', width:100, align:"center", formatter: "number", formatoptions: {decimalPlaces: 3}}
     	],
     	rowNum:100,
     	//rowList:[5,10,20],
      rownumbers: true,
     	pager: '#pager10_d',
     	sortname: 'name',
      viewrecords: true,
      sortorder: "asc",
  	//multiselect: true,
  	caption:"Portfolio Details"
  }).navGrid('#pager10_d',{add:false,edit:false,del:false});

});
