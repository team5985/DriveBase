package frc.robot.motorchecking;

import com.ctre.phoenix.motorcontrol.Faults;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.PowerDistributionPanel;

public class TalonSRXCheck extends MotorCheck{
    WPI_TalonSRX m_talon;
    int scancount;
    PowerDistributionPanel m_PDP;
    int m_controllerChannel;
    int faultScanCount;
    boolean faultOnLastScan;
    public TalonSRXCheck(WPI_TalonSRX talon, int controllerChannel, PowerDistributionPanel pdp) {
        m_talon = talon;
        m_PDP = pdp;
        m_controllerChannel = controllerChannel;
        scancount = 0;
        faultScanCount = 0;
        faultOnLastScan = false;
    }


    private int checkMotor(){
    
    if(Math.abs(m_talon.get()) >= motorCheckConstants.kMinimumSpeedForCheck){
        if(scancount >= 15){
            if(checkControllerComms() == 2 || checkControllerError() == 2 || checkControllerBusVoltage() == 2 || checkEncoderRotations() == 2 || checkMotorTemperature() == 2 || checkMotorCurrent() == 2 || checkPDPCurrent() == 2)
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
            else if(checkControllerComms() == 1 || checkControllerError() == 1 || checkControllerBusVoltage() == 1 || checkEncoderRotations() == 1 || checkMotorTemperature() == 1 || checkMotorCurrent() == 1 || checkPDPCurrent() == 1)
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
        if(Math.abs(m_talon.get()) >= motorCheckConstants.kMinimumSpeedForCheck){
        scancount++;
        }
    }   
    private int myStatus = 0;
    
    public int getStatus()
    {
        return myStatus;
    }

    private int checkControllerComms() {
        if (m_talon.getDeviceID() < 0) {
            return 2;
        } else {
            return 0;
        }
    }

    private int checkControllerError() {
        Faults faults = new Faults();
        m_talon.getFaults(faults);
        if (faults.hasAnyFault()) {
            return 2;
        } else {
            return 0;
        }

    }

    private int checkControllerBusVoltage() {
        if (m_talon.getBusVoltage() < motorCheckConstants.kControllerBusVoltageShutdown) {
            return 2;
        } else if (m_talon.getBusVoltage() < motorCheckConstants.kControllerBusVoltageWarning
                && m_talon.getBusVoltage() >= motorCheckConstants.kControllerBusVoltageShutdown) {
            return 1;
        } else {
            return 0;
        }
    }

    private int checkMotorCurrent() {
        if (m_talon.getSupplyCurrent() < motorCheckConstants.kMotorOutputCurrentShutdown) {
            return 2;
        } else if (m_talon.getSupplyCurrent() < motorCheckConstants.kMotorOutputCurrentWarning
                && m_talon.getSupplyCurrent() >= motorCheckConstants.kMotorOutputCurrentShutdown) {
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
        if (Math.sqrt(Math.abs(m_talon.getActiveTrajectoryVelocity())) <= motorCheckConstants.kEncoderVelocityShutdown) {
            return 2;
        } else if (Math.abs(m_talon.get()) <= motorCheckConstants.kEncoderVelocityWarning && Math.abs(m_talon.getActiveTrajectoryVelocity()) > motorCheckConstants.kEncoderVelocityShutdown) {
            return 1;
        } else {
            return 0;
        }
    }

    private int checkMotorTemperature() {
        if (m_talon.getTemperature() >= motorCheckConstants.kMotorTemperatureShutdown) {
            return 2;
        } else if (m_talon.getTemperature() >= motorCheckConstants.kMotorTemperatureWarning
                && m_talon.getTemperature() < motorCheckConstants.kMotorTemperatureShutdown) {
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