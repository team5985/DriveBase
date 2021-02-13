package frc.robot;

import com.ctre.phoenix.motorcontrol.Faults;
import com.ctre.phoenix.motorcontrol.StickyFaults;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;

import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.util.WPILibVersion;

public class VictorSPXCheck {
    WPI_VictorSPX m_victor;
    int scancount;
    PowerDistributionPanel m_PDP;
    int m_controllerChannel;
    public VictorSPXCheck(WPI_VictorSPX victor) {
        m_victor = victor;
        scancount = 0;
    }


    public int victorSPXMotorCheck(){
    
    if(Math.abs(m_victor.get()) >= kMinimumSpeedForCheck){
        if(scancount >= 15){
            if(checkControllerComms() == 2 || checkControllerError() == 2 || checkControllerBusVoltage() == 2 || checkEncoderRotations() == 2 || checkMotorTemperature() == 2 || checkPDPCurrent() == 2)
            {
            return 2;
            }
            else if(checkControllerComms() == 1 || checkControllerError() == 1 || checkControllerBusVoltage() == 1 || checkEncoderRotations() == 1 || checkMotorTemperature() == 1 || checkPDPCurrent() == 1)
            {
            return 1;
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
        victorSPXMotorCheck();
        if(Math.abs(m_victor.get()) >= kMinimumSpeedForCheck){
        scancount++;
        }
    }   

    private int checkControllerComms() {
        if (m_victor.getDeviceID() < 0) {
            return 2;
        } else {
            return 0;
        }
    }
//FIXME
    private int checkControllerError() {
        Faults faults = null;
        m_victor.getFaults(faults);
        if (faults.hasAnyFault()) {
            return 2;
        } else {
            return 0;
        }

    }

    private int checkControllerBusVoltage() {
        if (m_victor.getBusVoltage() < kControllerBusVoltageShutdown) {
            return 2;
        } else if (m_victor.getBusVoltage() < kControllerBusVoltageWarning
                && m_victor.getBusVoltage() >= kControllerBusVoltageShutdown) {
            return 1;
        } else {
            return 0;
        }
    }
    //FIXME
    // MAKE SURE TO TEST
    /*private int checkMotorCurrent() {
        if (m_victor.getSupplyCurrent() < kMotorOutputCurrentShutdown) {
            return 2;
        } else if (m_victor.getSupplyCurrent() < kMotorOutputCurrentWarning
                && m_victor.getSupplyCurrent() >= kMotorOutputCurrentShutdown) {
            return 1;
        } else {
            return 0;
        }

    } */
    //FIXME
    private int checkPDPCurrent() {
        if (m_PDP.getCurrent(m_controllerChannel) < kPDPOutputCurrentShutdown) {
            return 2;
        } else if (m_PDP.getCurrent(m_controllerChannel) < kPDPOutputCurrentWarning
                && m_PDP.getCurrent(m_controllerChannel) >= kPDPOutputCurrentShutdown) {
            return 1;
        } else {
            return 0;
        }
    }
    private int checkEncoderRotations() {
        if (Math.sqrt(Math.abs(m_victor.getActiveTrajectoryVelocity())) <= kEncoderVelocityShutdown) {
            return 2;
        } else if (Math.abs(m_victor.get()) <= kEncoderVelocityWarning && Math.abs(m_victor.getActiveTrajectoryVelocity()) > kEncoderVelocityShutdown) {
            return 1;
        } else {
            return 0;
        }
    }

    public int checkMotorTemperature() {
        if (m_victor.getTemperature() >= kMotorTemperatureShutdown) {
            return 2;
        } else if (m_victor.getTemperature() >= kMotorTemperatureWarning
                && m_victor.getTemperature() < kMotorTemperatureShutdown) {
            return 1;
        } else {
            return 0;
        }
    }


    private final int kMotorTemperatureShutdown = 80;
    private final int kMotorTemperatureWarning = 55;
    private final int kPDPOutputCurrentShutdown = 4;
    private final int kPDPOutputCurrentWarning = 7;
    private final int kMotorOutputCurrentShutdown = 20;
    private final int kMotorOutputCurrentWarning = 25;
    private final int kControllerBusVoltageShutdown = 10;
    private final int kControllerBusVoltageWarning = 12;
    private final int kEncoderVelocityShutdown = 0;
    private final int kEncoderVelocityWarning = 5;
    private final double kMinimumSpeedForCheck = 0.1;
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