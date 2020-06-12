
var name,data,title,size,dir = 0;
google.charts.load('current', {packages:['corechart','map'], mapsApiKey: 'AIzaSyAecvGHfAJxpDGh8iVSbPE-jjNuFDI_9Qw'});
function init(Name,Data,Title,Size){
  name=Name
  data=Data
  title=Title
  size=Size
  

  google.charts.setOnLoadCallback(drawchart);
}

function drawMap() {
      var data = google.visualization.arrayToDataTable([
       ['Lat', 'Long', 'Name'],
      [49.9967288, 36.233668, 'Метеостанція'],
      ]);

    var options = {
      zoomLevel: 12,
      showTooltip: true,
      showInfoWindow: true,
      useMapTypeControl: true
    };

    var map = new google.visualization.Map(document.getElementById('Map'));

    map.draw(data, options);
};

function drawchart(){
  drawDirection = drawdirection;
  drawDirection(dir)
  console.log(data)
  console.log(title)
  console.log(size)
  console.log(name)
  console.log(typeof(name))
  if(typeof(name) == "string"){
    name = name.split(",")
  }
  console.log(typeof(name))
  console.log(name)
        var options = {
          title: "",
          hAxis: {title: 'Час (GTM +0)',  titleTextStyle: {color: '#333'}},
          vAxis: {minValue: 0},
        };
      

    for(var i = 0; i<size; i++){
      console.log(name[i])
      if(name.split(",")[i]){

      }
      var chart = new google.visualization.AreaChart(document.getElementById(name.split(",")[i]));
      options.title = title[i] 
      var dataTable=new google.visualization.arrayToDataTable(data[i]) 
      chart.draw(dataTable, options);
      drawMap()
    }
        

}

var drawDirection = (angle) =>{
  dir = angle;
}
function drawdirection(angle) {
        var data = google.visualization.arrayToDataTable([
          ['none', 'none'],
          [angle + '°',  358],
          ['Arrow',  2],

        ]);

      var options = {
        legend: 'none',
        pieSliceText: 'label',
        title: 'Напрям флюгера',
        pieStartAngle: angle+1
      };

        var chart = new google.visualization.PieChart(document.getElementById('ind_direction'));
        chart.draw(data, options);
      }

let ws = new WebSocket("ws://meteostation.pp.ua/ws")


ws.onmessage = (message) => {
  var field = document.getElementById("text");
  field.innerHTML = field.innerHTML + "\n" + message.data
  let packet = JSON.parse(message.data);
  if(packet.purpose == "initChart"){
    init(packet.data.name,packet.data.data,packet.data.title,packet.data.size);
  }else if(packet.purpose == "updateIndications"){
    document.getElementById('ind_anemeter').innerHTML = packet.data.anemeter;
    document.getElementById('ind_rainmeter').innerHTML = packet.data.rainmeter;
    document.getElementById('ind_temperature').innerHTML = packet.data.temperature;
    document.getElementById('ind_humidity').innerHTML = packet.data.humidity;
    document.getElementById('ind_pressure').innerHTML = packet.data.pressure;
    drawDirection(packet.data.direction);
    var timestamp = new Date(packet.data.timestamp)
    var options = { year: 'numeric', month: 'long', day: 'numeric', hour: 'numeric', minute: 'numeric', second: 'numeric'};
    document.getElementById('timestamp').innerHTML = timestamp.toLocaleString("ru",options);
  }


}
ws.onopen = (event) => {
  ws.send("test123")
}
ws.onclose = (event) => {
  alert("Соединение разорвано " + event.code+ " " + event.CloseEvent)
}
ws.onerror = (error) => {
  alert("[error] " +error.message)
}

