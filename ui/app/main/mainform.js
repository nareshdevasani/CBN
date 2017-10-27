$(document).ready(function() {

  console.log("document ready called...");

  $( "input[type='checkbox']" ).checkboxradio();

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

              var oClone = document.getElementById("checkbox-1").cloneNode(true);
              var labelClone = document.getElementById("cb-label-1").cloneNode(true);
              var container = $('#cblist_group');
              var inputs = container.find('input');
              oClone.id = ui.item.value + "-" + inputs.length + 1;
              labelClone.id = "label-" + oClone.id;
              labelClone.setAttribute("for", oClone.id);
              oClone.setAttribute("name", oClone.id);
              labelClone.innerHTML = ui.item.label;
              document.getElementById("cblist_group").appendChild(labelClone);
              document.getElementById("cblist_group").appendChild(oClone);

              $( "input[type='checkbox']" ).checkboxradio();
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
  	height: 550,
    url:'',
  	datatype: "json",
     	colNames:['Instrument Name', 'Percent'],
     	colModel:[
     		{name:'name',index:'name', width:350},
     		{name:'percent', index:'percent', width:100, align:"center", formatter: "number", formatoptions: {decimalPlaces: 3}}
     	],
     	rowNum:1000,
     	//rowList:[5,10,20],
      rownumbers: true,
      footerrow: true,
     	pager: '#pager10_d',
     	sortname: 'name',
      viewrecords: true,
      sortorder: "asc",
  	//multiselect: true,
  	caption:"Portfolio Details",
    loadComplete : function() {
      var grid = $("#portfolio-grid"),
          sum = grid.jqGrid('getCol', 'percent', false, 'sum');
      grid.jqGrid('footerData','set', {name: 'Total:', percent: sum});
    }
  }).navGrid('#pager10_d',{add:false,edit:false,del:false});

});
