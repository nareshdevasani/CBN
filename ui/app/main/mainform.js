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

              var oClone = document.getElementById("checkbox-1").cloneNode(true),
                  labelClone = document.getElementById("cb-label-1").cloneNode(true),
                  cbListGroupId = "cblist_group",
                  index = window.groupCount || 0;

              var container = $('#' + cbListGroupId + index),
                  inputs = container.find('input');
              oClone.id = ui.item.value + "-" + inputs.length + 1;
              oClone.value = ui.item.value;
              labelClone.id = "label-" + oClone.id;
              labelClone.setAttribute("for", oClone.id);
              labelClone.style.textAlign = "left";
              oClone.setAttribute("name", oClone.id);
              labelClone.innerHTML = ui.item.label;
              oClone.checked = true;
              document.getElementById(cbListGroupId + index).appendChild(labelClone);
              document.getElementById(cbListGroupId + index).appendChild(oClone);

              var queryParamFun = function () {
                var queryParam = "", size = window.groupCount || 0;
                for (var i = 0; i <= size; i++) {
                  var groupSchemes = "";
                  $("#cblist_group" + i +" input:checkbox:checked").each(function() {
                    if (groupSchemes.length > 1) {
                      if (i == size) {
                        groupSchemes += "&schemecode=";
                      } else {
                        groupSchemes += "-";
                      }
                    }
                    groupSchemes += $(this).val();
                  });
                  if (queryParam.length > 1) {
                    queryParam += "&";
                  }
                  queryParam += "schemecode=" + groupSchemes;
                }

                return queryParam;
              };
              var reloadDataFun = function () {
                $("#portfolio-grid").jqGrid('setGridParam',{url:"freecom/apis/funds/fund-portfolio?" + queryParamFun()}).trigger('reloadGrid');
                $.ajax( {
                  url: "freecom/apis/funds/portfolio-venn-sets?" + queryParamFun(),
                  dataType: "json",
                  success: function( data ) {
                    // remove and add venn div back.
                    d3.select("#ui-widget-venn").remove();
                    //d3.select(".venntooltip").remove();
                    // add div
                    d3.select(".article").append("div").attr("id", "ui-widget-venn");

                    var div = d3.select("#ui-widget-venn"),
                        chart = venn.VennDiagram().width(500).height(500);

                    div.datum(data).call(chart);

                    //var tooltip = d3.select("body").append("div").attr("class", "venntooltip");

                    div.selectAll("path").style("stroke-opacity", 0).style("stroke", "#fff").style("stroke-width", 3)
                    div.selectAll("g").on("mouseover", function(d, i) {
                          // sort all the areas relative to the current item
                          venn.sortAreas(div, d);
                          var tooltip = d3.select(".venntooltip");
                          // Display a tooltip with the current size
                          tooltip.transition().duration(400).style("opacity", .9);
                          tooltip.text(d.size + " instruments");
                          // highlight the current path
                          var selection = d3.select(this).transition("tooltip").duration(400);
                          selection.select("path")
                              .style("fill-opacity", d.sets.length == 1 ? .4 : .1)
                              .style("stroke-opacity", 1);
                      })
                      .on("mousemove", function() {
                          var tooltip = d3.select(".venntooltip");
                          tooltip.style("left", (d3.event.pageX) + "px")
                                 .style("top", (d3.event.pageY - 28) + "px");
                      })
                      .on("mouseout", function(d, i) {
                          var tooltip = d3.select(".venntooltip");
                          tooltip.transition().duration(400).style("opacity", 0);
                          var selection = d3.select(this).transition("tooltip").duration(400);
                          selection.select("path")
                              .style("fill-opacity", d.sets.length == 1 ? .25 : .0)
                              .style("stroke-opacity", 0);
                      });
                  }
                });
              };
              $( "input[type='checkbox']" ).checkboxradio();
              $( "input[type='checkbox']" ).on("change", function() {
                reloadDataFun();
              });
              reloadDataFun();
            //  $("#portfolio-grid").jqGrid('setGridParam',{url:"freecom/apis/funds/fund-portfolio?" + queryParamFun()}).trigger('reloadGrid');
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

  $("#make-grp-btn").button().on("click", function() {
    window.groupCount = (window.groupCount || 0) + 1;
    //console.log(window.cblistgrpname + window.groupCount);
    d3.select("#cblist").append("fieldset").attr("id", "cblist_group" + window.groupCount);
    d3.select("#cblist_group"+window.groupCount).append("legend").innerHTML="More schemes...";
  });
});
