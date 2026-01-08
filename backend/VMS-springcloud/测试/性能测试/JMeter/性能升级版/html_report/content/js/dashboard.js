/*
   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
var showControllersOnly = false;
var seriesFilter = "";
var filtersOnlySampleSeries = true;

/*
 * Add header in statistics table to group metrics by category
 * format
 *
 */
function summaryTableHeader(header) {
    var newRow = header.insertRow(-1);
    newRow.className = "tablesorter-no-sort";
    var cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 1;
    cell.innerHTML = "Requests";
    newRow.appendChild(cell);

    cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 3;
    cell.innerHTML = "Executions";
    newRow.appendChild(cell);

    cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 7;
    cell.innerHTML = "Response Times (ms)";
    newRow.appendChild(cell);

    cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 1;
    cell.innerHTML = "Throughput";
    newRow.appendChild(cell);

    cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 2;
    cell.innerHTML = "Network (KB/sec)";
    newRow.appendChild(cell);
}

/*
 * Populates the table identified by id parameter with the specified data and
 * format
 *
 */
function createTable(table, info, formatter, defaultSorts, seriesIndex, headerCreator) {
    var tableRef = table[0];

    // Create header and populate it with data.titles array
    var header = tableRef.createTHead();

    // Call callback is available
    if(headerCreator) {
        headerCreator(header);
    }

    var newRow = header.insertRow(-1);
    for (var index = 0; index < info.titles.length; index++) {
        var cell = document.createElement('th');
        cell.innerHTML = info.titles[index];
        newRow.appendChild(cell);
    }

    var tBody;

    // Create overall body if defined
    if(info.overall){
        tBody = document.createElement('tbody');
        tBody.className = "tablesorter-no-sort";
        tableRef.appendChild(tBody);
        var newRow = tBody.insertRow(-1);
        var data = info.overall.data;
        for(var index=0;index < data.length; index++){
            var cell = newRow.insertCell(-1);
            cell.innerHTML = formatter ? formatter(index, data[index]): data[index];
        }
    }

    // Create regular body
    tBody = document.createElement('tbody');
    tableRef.appendChild(tBody);

    var regexp;
    if(seriesFilter) {
        regexp = new RegExp(seriesFilter, 'i');
    }
    // Populate body with data.items array
    for(var index=0; index < info.items.length; index++){
        var item = info.items[index];
        if((!regexp || filtersOnlySampleSeries && !info.supportsControllersDiscrimination || regexp.test(item.data[seriesIndex]))
                &&
                (!showControllersOnly || !info.supportsControllersDiscrimination || item.isController)){
            if(item.data.length > 0) {
                var newRow = tBody.insertRow(-1);
                for(var col=0; col < item.data.length; col++){
                    var cell = newRow.insertCell(-1);
                    cell.innerHTML = formatter ? formatter(col, item.data[col]) : item.data[col];
                }
            }
        }
    }

    // Add support of columns sort
    table.tablesorter({sortList : defaultSorts});
}

$(document).ready(function() {

    // Customize table sorter default options
    $.extend( $.tablesorter.defaults, {
        theme: 'blue',
        cssInfoBlock: "tablesorter-no-sort",
        widthFixed: true,
        widgets: ['zebra']
    });

    var data = {"OkPercent": 99.97742954794609, "KoPercent": 0.022570452053911137};
    var dataset = [
        {
            "label" : "FAIL",
            "data" : data.KoPercent,
            "color" : "#FF6347"
        },
        {
            "label" : "PASS",
            "data" : data.OkPercent,
            "color" : "#9ACD32"
        }];
    $.plot($("#flot-requests-summary"), dataset, {
        series : {
            pie : {
                show : true,
                radius : 1,
                label : {
                    show : true,
                    radius : 3 / 4,
                    formatter : function(label, series) {
                        return '<div style="font-size:8pt;text-align:center;padding:2px;color:white;">'
                            + label
                            + '<br/>'
                            + Math.round10(series.percent, -2)
                            + '%</div>';
                    },
                    background : {
                        opacity : 0.5,
                        color : '#000'
                    }
                }
            }
        },
        legend : {
            show : true
        }
    });

    // Creates APDEX table
    createTable($("#apdexTable"), {"supportsControllersDiscrimination": true, "overall": {"data": [0.26504338091239854, 500, 1500, "Total"], "isController": false}, "titles": ["Apdex", "T (Toleration threshold)", "F (Frustration threshold)", "Label"], "items": [{"data": [0.258830602735608, 500, 1500, "获取用户总数"], "isController": false}, {"data": [0.007594936708860759, 500, 1500, "步骤1-用户登录"], "isController": false}, {"data": [0.006887052341597796, 500, 1500, "用户登录"], "isController": false}, {"data": [0.5204067121729238, 500, 1500, "根据ID获取用户信息"], "isController": false}, {"data": [0.0155, 500, 1500, "登录获取Token"], "isController": false}, {"data": [0.19036144578313252, 500, 1500, "用户注册"], "isController": false}, {"data": [0.40315503552026744, 500, 1500, "步骤2-获取当前用户信息"], "isController": false}, {"data": [0.4056010069225928, 500, 1500, "步骤3-获取用户权限"], "isController": false}, {"data": [0.016679447852760737, 500, 1500, "完整用户流程"], "isController": true}]}, function(index, item){
        switch(index){
            case 0:
                item = item.toFixed(3);
                break;
            case 1:
            case 2:
                item = formatDuration(item);
                break;
        }
        return item;
    }, [[0, 0]], 3);

    // Create statistics table
    createTable($("#statisticsTable"), {"supportsControllersDiscrimination": true, "overall": {"data": ["Total", 31014, 7, 0.022570452053911137, 3794.3408138260197, 0, 49984, 2484.0, 10589.900000000001, 14420.650000000005, 23492.88000000002, 167.28516257092926, 200.1270886035837, 63.08573417677567], "isController": false}, "titles": ["Label", "#Samples", "FAIL", "Error %", "Average", "Min", "Max", "Median", "90th pct", "95th pct", "99th pct", "Transactions/s", "Received", "Sent"], "items": [{"data": ["获取用户总数", 6653, 1, 0.015030813166992334, 4219.598076055921, 7, 31317, 2690.0, 10489.0, 12725.59999999997, 18294.420000000002, 74.84531443356958, 15.987335558696142, 31.13682026240297], "isController": false}, {"data": ["步骤1-用户登录", 5135, 2, 0.03894839337877313, 8496.296202531652, 396, 49984, 7291.0, 14937.60000000003, 19171.6, 28366.280000000024, 27.80635728596957, 23.865718452171443, 6.924434675705313], "isController": false}, {"data": ["用户登录", 726, 0, 0.0, 10307.05922865013, 601, 38418, 8966.5, 19194.6, 22322.149999999998, 31719.870000000006, 5.689120145440867, 4.887029077888443, 1.4167242549681849], "isController": false}, {"data": ["根据ID获取用户信息", 7032, 0, 0.0, 1241.0826222980634, 11, 10363, 907.0, 2959.0999999999995, 3203.0, 4180.510000000004, 88.8563161020483, 48.53854600370867, 37.061150910107536], "isController": false}, {"data": ["登录获取Token", 1000, 0, 0.0, 11946.804000000004, 424, 39400, 10940.5, 22382.8, 25512.599999999995, 34089.030000000006, 9.51492892348094, 8.172812889517402, 2.369440308093399], "isController": false}, {"data": ["用户注册", 415, 0, 0.0, 3942.4795180722876, 103, 25402, 2203.0, 9339.800000000003, 11817.4, 20789.2, 5.061037329723534, 1.5003160290064514, 1.64582561601361], "isController": false}, {"data": ["步骤2-获取当前用户信息", 4786, 2, 0.041788549937317176, 1373.5806519013827, 2, 10785, 1268.0, 2664.3, 3090.2999999999993, 4157.040000000001, 28.484026091510735, 22.406600721846285, 11.819716788970624], "isController": false}, {"data": ["步骤3-获取用户权限", 4767, 2, 0.04195510803440319, 1422.2865533878817, 2, 7679, 1212.0, 2691.0, 3191.0, 5492.199999999997, 28.634242156668407, 128.6586763101502, 12.077783177607985], "isController": false}, {"data": ["完整用户流程", 5216, 2, 0.03834355828220859, 11008.67427147238, 0, 52588, 9684.5, 18686.0, 22917.15, 32425.85999999999, 28.12269171254037, 159.54395884946865, 28.443108759428593], "isController": true}]}, function(index, item){
        switch(index){
            // Errors pct
            case 3:
                item = item.toFixed(2) + '%';
                break;
            // Mean
            case 4:
            // Mean
            case 7:
            // Median
            case 8:
            // Percentile 1
            case 9:
            // Percentile 2
            case 10:
            // Percentile 3
            case 11:
            // Throughput
            case 12:
            // Kbytes/s
            case 13:
            // Sent Kbytes/s
                item = item.toFixed(2);
                break;
        }
        return item;
    }, [[0, 0]], 0, summaryTableHeader);

    // Create error table
    createTable($("#errorsTable"), {"supportsControllersDiscrimination": false, "titles": ["Type of error", "Number of errors", "% in errors", "% in all samples"], "items": [{"data": ["500/Internal Server Error", 3, 42.857142857142854, 0.00967305088024763], "isController": false}, {"data": ["401/Unauthorized", 4, 57.142857142857146, 0.012897401173663506], "isController": false}]}, function(index, item){
        switch(index){
            case 2:
            case 3:
                item = item.toFixed(2) + '%';
                break;
        }
        return item;
    }, [[1, 1]]);

        // Create top5 errors by sampler
    createTable($("#top5ErrorsBySamplerTable"), {"supportsControllersDiscrimination": false, "overall": {"data": ["Total", 31014, 7, "401/Unauthorized", 4, "500/Internal Server Error", 3, "", "", "", "", "", ""], "isController": false}, "titles": ["Sample", "#Samples", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors"], "items": [{"data": ["获取用户总数", 6653, 1, "500/Internal Server Error", 1, "", "", "", "", "", "", "", ""], "isController": false}, {"data": ["步骤1-用户登录", 5135, 2, "500/Internal Server Error", 2, "", "", "", "", "", "", "", ""], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": ["步骤2-获取当前用户信息", 4786, 2, "401/Unauthorized", 2, "", "", "", "", "", "", "", ""], "isController": false}, {"data": ["步骤3-获取用户权限", 4767, 2, "401/Unauthorized", 2, "", "", "", "", "", "", "", ""], "isController": false}, {"data": [], "isController": false}]}, function(index, item){
        return item;
    }, [[0, 0]], 0);

});
