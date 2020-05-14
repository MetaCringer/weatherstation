
var name,data,title,size
function init(Name,Data,Title,Size){
  name=Name
  data=Data
  title=Title
  size=Size
  google.charts.load('current', {packages:['corechart']});
      google.charts.setOnLoadCallback(drawchart);
}

function drawchart(){
  
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
      var chart = new google.visualization.AreaChart(document.getElementById(name.split(",")[i]));
      options.title = title[i] 
      var dataTable=new google.visualization.arrayToDataTable(data[i]) 
      chart.draw(dataTable, options);
    }
        

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

