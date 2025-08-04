package com.example.ambulancealert.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.example.ambulancealert.model.AccidentHistory;
import com.example.ambulancealert.model.AccidentReport;
import com.example.ambulancealert.model.Ambulance;
import com.example.ambulancealert.model.HospitalNotification;
import com.example.ambulancealert.model.User;
import com.example.ambulancealert.repository.AccidentHistoryRepository;
import com.example.ambulancealert.repository.UserRepository;
import com.example.ambulancealert.service.AmbulanceService;
import com.example.ambulancealert.service.AmbulanceSocketHandler;


@RestController
@RequestMapping("/api/accident")
@CrossOrigin(origins = "http://localhost:4200") 
public class AccidentController {
    @Autowired
    private AmbulanceSocketHandler socketHandler;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private AmbulanceService ambulanceService;

    @Autowired
    private AccidentHistoryRepository accidentHistoryRepository;

    @GetMapping("/history")
    public ResponseEntity<?> getHistory(@RequestParam String deviceId) {
        User user = userRepository.findByDeviceId(deviceId);
        if (user == null) {
            return ResponseEntity.status(404).body(Map.of("error", "User not found"));
        }
        return ResponseEntity.ok(accidentHistoryRepository.findByUserOrderByTimestampDesc(user));
    }

    @PostMapping("/report")
    public ResponseEntity<Map<String,String>> reportAccident(@RequestBody AccidentReport report) {
        User user=userRepository.findByDeviceId(report.getDeviceId());

        if(user==null){
          Map<String, String> errorBody = new HashMap<>();
    errorBody.put("error", "User not registered.");
    return ResponseEntity.status(404).body(errorBody);
        }
        System.out.println("🚨 SOS from: " + user.getName()+"("+user.getPhoneNumber()+')');
        System.out.println("📞 Emergency Contact: " + user.getEmergencyContact());

        Ambulance assigned = ambulanceService.dispatchAmbulance();
        if (assigned != null) {
            HospitalNotification notification = new HospitalNotification(
                assigned.getId(),
                report.getDeviceId(),
                "In Transit",
                10
            );

            String dispatchMessage = String.format(
                "{\"ambulanceId\":\"%s\", \"latitude\":%f, \"longitude\":%f, \"status\":\"dispatched\"}",
                assigned.getId(), assigned.getLatitude(), assigned.getLongitude()
            );
            socketHandler.broadcast(dispatchMessage);

             AccidentHistory historyRecord = new AccidentHistory();
            historyRecord.setUser(user);
            historyRecord.setTimestamp(LocalDateTime.now());
            historyRecord.setLatitude(report.getLatitude());
            historyRecord.setLongitude(report.getLongitude());
            historyRecord.setAmbulanceId(assigned.getId());
            accidentHistoryRepository.save(historyRecord);

            Map<String, String> responseBody = new HashMap<>();
        responseBody.put("message", "Ambulance " + assigned.getId() + " dispatched!");
        responseBody.put("ambulanceId", assigned.getId());


            return ResponseEntity.ok(responseBody);
        } else {
           Map<String, String> errorBody = new HashMap<>();
        errorBody.put("error", "No ambulance available at the moment.");
        return ResponseEntity.status(503).body(errorBody);
        }
    }
}
