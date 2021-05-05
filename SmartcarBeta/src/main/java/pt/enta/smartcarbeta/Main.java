package pt.enta.smartcarbeta;

import static spark.Spark.*;
import com.google.gson.Gson;
import com.smartcar.sdk.*;
import com.smartcar.sdk.data.*;

public class Main {

    // global variable to save our accessToken
    private static String access;
    private static Gson gson = new Gson();

    public static void main(String[] args) {
        port(8000);
        String clientId = "48def061-6792-44e2-9347-332ef70aac55";
        String clientSecret = "393f3e6d-4e4f-4871-a08f-78da1d516d34";
        String redirectUri = "http://localhost:8000/callback";
        String[] scope = {"read_engine_oil read_battery read_charge read_fuel read_location control_security read_odometer read_tires read_vehicle_info read_vin"};
        boolean testMode = true;

        AuthClient client = new AuthClient(
                clientId,
                clientSecret,
                redirectUri,
                scope,
                testMode
        );

        get("/login", (req, res) -> {
            AuthClient.AuthUrlBuilder link = client.authUrlBuilder();
            res.redirect(link.build());
            return null;
        });

        get("/callback", (req, res) -> {
            String code = req.queryMap("code").value();
            Auth auth = client.exchangeCode(code);
            // in a production app you'll want to store this in some kind of persistent storage
            access = auth.getAccessToken();
            return "Thank you for registering!";
        });

        get("/vehicle", (req, res) -> {
            SmartcarResponse<VehicleIds> vehicleIdResponse = AuthClient.getVehicleIds(access);
            // the list of vehicle ids
            String[] vehicleIds = vehicleIdResponse.getData().getVehicleIds();
            // instantiate the first vehicle in the vehicle id list
            Vehicle vehicle = new Vehicle(vehicleIds[0], access);
            VehicleInfo info = vehicle.info();
            System.out.println(gson.toJson(info));
            res.type("application/json");
            return gson.toJson(info);
        });

        get("/vehicles", (req, res) -> {
            SmartcarResponse<VehicleIds> vehicleIdResponse = AuthClient.getVehicleIds(access);
            // the list of vehicle ids
            String[] vehicleIds = vehicleIdResponse.getData().getVehicleIds();
            String car = "";
            // instantiate the first vehicle in the vehicle id list
            for (String v : vehicleIds) {
                Vehicle vehicle = new Vehicle(v, access);
                VehicleInfo info = vehicle.info();
                car = car + gson.toJson(info);
                System.out.println(gson.toJson(info));
            }
            res.type("application/json");
            return car;
        });

        get("/odometer", (req, res) -> {
            String line = "";
            SmartcarResponse<VehicleIds> vehicleIdsResponse = AuthClient.getVehicleIds(access);
            String[] vehicleIds = vehicleIdsResponse.getData().getVehicleIds();
            /*for (int x = 0; x < vehicleIds.length; x++) {*/
            Vehicle vehicle = new Vehicle(vehicleIds[0], access);
            String vin = vehicle.vin();
            SmartcarResponse<VehicleOdometer> odometerResponse = vehicle.odometer();
            VehicleOdometer odometerData = odometerResponse.getData();
            double odometer = odometerData.getDistance();
            line = "O carro " + vin + " tem " + odometer + " quilómetros.";
            System.out.println(line);
            System.out.println(gson.toJson(line));
            res.type("application/json");
            return gson.toJson(line);
        });

        get("/location", (req, res) -> {
            String line = "";
            String line1 = "";
            SmartcarResponse<VehicleIds> vehicleIdsResponse = AuthClient.getVehicleIds(access);
            String[] vehicleIds = vehicleIdsResponse.getData().getVehicleIds();
            Vehicle vehicle = new Vehicle(vehicleIds[0], access);
            String vin = vehicle.vin();
            SmartcarResponse<VehicleLocation> locationResponse = vehicle.location();
            VehicleLocation locationData = locationResponse.getData();
            line = "O carro " + vin + " tem como localização " + locationData.getLatitude() + " de latitude e " + locationData.getLongitude() + " de longitude.";
            System.out.println(line);
            System.out.println(gson.toJson(line));
            res.type("application/json");
            return gson.toJson(line + line1);
        });

        get("/fuel", (req, res) -> {
            String line = "";
            String line1 = "";
            String line2 = "";
            SmartcarResponse<VehicleIds> vehicleIdsResponse = AuthClient.getVehicleIds(access);
            String[] vehicleIds = vehicleIdsResponse.getData().getVehicleIds();
            Vehicle vehicle = new Vehicle(vehicleIds[0], access);
            String vin = vehicle.vin();
            SmartcarResponse<VehicleFuel> fuelResponse = vehicle.fuel();
            VehicleFuel fuelData = fuelResponse.getData();
            line = "O carro " + vin + " encontra-se com " + fuelData.getAmountRemaining() + " gallons no depósito.";
            line1 = " Corresponde a " + fuelData.getPercentRemaining() + " do depósito.";
            line2 = " Assim o carro pode percorrer estimadamente mais " + fuelData.getRange() + " quilómetros.";
            System.out.println(line);
            System.out.println(gson.toJson(line));
            res.type("application/json");
            return gson.toJson(line + line1 + line2);
        });

        get("/engine", (req, res) -> {
            String line = "";
            SmartcarResponse<VehicleIds> vehicleIdsResponse = AuthClient.getVehicleIds(access);
            String[] vehicleIds = vehicleIdsResponse.getData().getVehicleIds();
            Vehicle vehicle = new Vehicle(vehicleIds[0], access);
            String vin = vehicle.vin();
            SmartcarResponse<VehicleOil> oilResponse = vehicle.oil();
            VehicleOil oilData = oilResponse.getData();
            double oil = oilData.getLifeRemaining();
            line = "A vida útil restante do óleo do motor, em percentagem, é de " + oilData.getLifeRemaining() + ".";
            System.out.println(line);
            System.out.println(gson.toJson(line));
            res.type("application/json");
            return gson.toJson(line);
        });

        get("/battery", (req, res) -> {
            String line = "";
            SmartcarResponse<VehicleIds> vehicleIdsResponse = AuthClient.getVehicleIds(access);
            String[] vehicleIds = vehicleIdsResponse.getData().getVehicleIds();
            Vehicle vehicle = new Vehicle(vehicleIds[0], access);
            String vin = vehicle.vin();
            SmartcarResponse<VehicleBattery> batteryResponse = vehicle.battery();
            VehicleBattery batteryData = batteryResponse.getData();
            double bateria = batteryData.getPercentRemaining();
            line = "Atualmente, o carro " + vin + " encontra-se com " + bateria + " de bateria.";
            System.out.println(line);
            System.out.println(gson.toJson(line));
            res.type("application/json");
            return gson.toJson(line);
        });

        get("/charging", (req, res) -> {
            String line = "";
            String line1 = "";
            SmartcarResponse<VehicleIds> vehicleIdsResponse = AuthClient.getVehicleIds(access);
            String[] vehicleIds = vehicleIdsResponse.getData().getVehicleIds();
            Vehicle vehicle = new Vehicle(vehicleIds[0], access);
            String vin = vehicle.vin();
            SmartcarResponse<VehicleCharge> chargeResponse = vehicle.charge();
            VehicleCharge chargeData = chargeResponse.getData();
            String state = chargeData.getState();
            boolean carregar = chargeData.getIsPluggedIn();
            line = "Atualmente, o carro " + vin + " está em carregamento " + carregar + ".";
            line1 = " Neste momento, a sua bateria está a " + state + ".";
            System.out.println(line);
            System.out.println(gson.toJson(line));
            res.type("application/json");
            return gson.toJson(line + line1);
        });

        get("/tire", (req, res) -> {
            String line = "";
            String line1 = "";
            String line2 = "";
            String line3 = "";
            SmartcarResponse<VehicleIds> vehicleIdsResponse = AuthClient.getVehicleIds(access);
            String[] vehicleIds = vehicleIdsResponse.getData().getVehicleIds();
            Vehicle vehicle = new Vehicle(vehicleIds[0], access);
            String vin = vehicle.vin();
            SmartcarResponse<VehicleTirePressure> tirePressure = vehicle.tirePressure();
            VehicleTirePressure tirePressureData = tirePressure.getData();
            double backleft = tirePressureData.getBackLeft();
            double backright = tirePressureData.getBackRight();
            double FrontLeft = tirePressureData.getFrontLeft();
            double FrontRight = tirePressureData.getFrontRight();
            line = "O carro encontra-se com a seguintes pressão em cada pneu respetivamente: " + vin + " o pneu de trás do lado esquerdo a " + backleft;
            line1 = " ,o pneu de trás do lado direito a " + backright;
            line2 = " ,o pneu da frente do lado esquerdo a " + FrontLeft;
            line3 = " e, por fim, o pneu da frente do lado direito a " + FrontRight + ".";
            System.out.println(line);
            System.out.println(gson.toJson(line));
            res.type("application/json");
            return gson.toJson(line + line1 + line2 + line3);
        });
    }
}
