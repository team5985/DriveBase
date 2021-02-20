package frc.robot.motorchecking;

import com.ctre.phoenix.motorcontrol.Faults;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;

import edu.wpi.first.wpilibj.PowerDistributionPanel;

public class VictorSPXCheck extends MotorCheck{
    WPI_VictorSPX m_victor;
    int scancount;
    PowerDistributionPanel m_PDP;
    int m_controllerChannel;
    int faultScanCount;
    boolean faultOnLastScan;
    public VictorSPXCheck(WPI_VictorSPX victor, int controllerChannel, PowerDistributionPanel pdp) {
        m_victor = victor;
        m_PDP = pdp;
        m_controllerChannel = controllerChannel;
        scancount = 0;
        faultScanCount = 0;
        faultOnLastScan = false;
    }


    private int checkMotor(){
    
    if(Math.abs(m_victor.get()) >= motorCheckConstants.kMinimumSpeedForCheck){
        if(scancount >= 15){
            if(checkControllerComms() == 2 || checkControllerError() == 2 || checkControllerBusVoltage() == 2 || checkEncoderRotations() == 2 || checkMotorTemperature() == 2 || checkPDPCurrent() == 2)
            {
                if(faultScanCount >= motorCheckConstants.faultScanCountMinimum){
                    return 2;
                }
                else if(faultScanCount > 0 && faultScanCount < motorCheckConstants.faultScanCountMinimum && faultOnLastScan == true){
                    faultScanCount++;
                    return 0;
                }
                else if(faultScanCount > 0 && faultOnLastScan == false){
                    faultScanCount = 0;
                    return 0;
                }
                else{
                faultScanCount++;
                faultOnLastScan = true;
                return 0;
                }
            }
            else if(checkControllerComms() == 1 || checkControllerError() == 1 || checkControllerBusVoltage() == 1 || checkEncoderRotations() == 1 || checkMotorTemperature() == 1 || checkPDPCurrent() == 1)
            {
                if(faultScanCount >= motorCheckConstants.faultScanCountMinimum){
                    return 1;
                }
                else if(faultScanCount > 0 && faultScanCount < motorCheckConstants.faultScanCountMinimum && faultOnLastScan == true){
                    faultScanCount++;
                    return 0;
                }
                else if(faultScanCount > 0 && faultOnLastScan == false){
                    faultScanCount = 0;
                    return 0;
                }
                else{
                faultScanCount++;
                faultOnLastScan = true;
                return 0;
                }
            }
            else
            {
            return 0;
            }
        }
        else{
        return 0;
        }
    }
    else{
    return 0;
    }
    }
        
    public void update() {
        myStatus = checkMotor();
        if(Math.abs(m_victor.get()) >= motorCheckConstants.kMinimumSpeedForCheck){
        scancount++;
        }
    }   
    private int myStatus = 0;
    
    public int getStatus()
    {
        return myStatus;
    }

    private int checkControllerComms() {
        if (m_victor.getDeviceID() < 0) {
            return 2;
        } else {
            return 0;
        }
    }

    private int checkControllerError() {
        Faults faults = new Faults();
        m_victor.getFaults(faults);
        if (faults.hasAnyFault()) {
            return 2;
        } else {
            return 0;
        }

    }

    private int checkControllerBusVoltage() {
        if (m_victor.getBusVoltage() < motorCheckConstants.kControllerBusVoltageShutdown) {
            return 2;
        } else if (m_victor.getBusVoltage() < motorCheckConstants.kControllerBusVoltageWarning
                && m_victor.getBusVoltage() >= motorCheckConstants.kControllerBusVoltageShutdown) {
            return 1;
        } else {
            return 0;
        }
    }
       private int checkPDPCurrent() {
        if (m_PDP.getCurrent(m_controllerChannel) < motorCheckConstants.kPDPOutputCurrentShutdown) {
            return 2;
        } else if (m_PDP.getCurrent(m_controllerChannel) < motorCheckConstants.kPDPOutputCurrentWarning
                && m_PDP.getCurrent(m_controllerChannel) >= motorCheckConstants.kPDPOutputCurrentShutdown) {
            return 1;
        } else {
            return 0;
        }
    }
    private int checkEncoderRotations() {
        if (Math.sqrt(Math.abs(m_victor.getActiveTrajectoryVelocity())) <= motorCheckConstants.kEncoderVelocityShutdown) {
            return 2;
        } else if (Math.abs(m_victor.get()) <= motorCheckConstants.kEncoderVelocityWarning && Math.abs(m_victor.getActiveTrajectoryVelocity()) > motorCheckConstants.kEncoderVelocityShutdown) {
            return 1;
        } else {
            return 0;
        }
    }

    private int checkMotorTemperature() {
        if (m_victor.getTemperature() >= motorCheckConstants.kMotorTemperatureShutdown) {
            return 2;
        } else if (m_victor.getTemperature() >= motorCheckConstants.kMotorTemperatureWarning
                && m_victor.getTemperature() < motorCheckConstants.kMotorTemperatureShutdown) {
            return 1;
        } else {
            return 0;
        }
    }


}


/*  
  private int[] errorArray = {};
public int[] sparkMaxMotorCheck(){ 
 errorArray[0] = checkControllerComms(m_spark); 
 errorArray[1] = checkControllerError(m_spark);
 errorArray[2] = checkControllerBusVoltage(m_spark); 
 errorArray[3] = checkMotorCurrent(m_spark); 
 errorArray[4] = checkPDPCurrent(m_PDP, m_controllerChannel); 
 errorArray[5] = checkEncoderRotations(m_spark, m_encoder);
 errorArray[6] = checkMotorTemperature(m_spark); return errorArray;
 }
 */