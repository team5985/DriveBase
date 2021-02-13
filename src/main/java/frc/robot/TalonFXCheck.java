package frc.robot;

import com.ctre.phoenix.motorcontrol.Faults;
import com.ctre.phoenix.motorcontrol.StickyFaults;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.util.WPILibVersion;

public class TalonFXCheck {
    WPI_TalonFX m_talon;
    int scancount;
    PowerDistributionPanel m_PDP;
    int m_controllerChannel;
    public TalonFXCheck(WPI_TalonFX talon) {
        m_talon = talon;
        scancount = 0;
    }


    public int talonFXMotorCheck(){
    
    if(Math.abs(m_talon.get()) >= kMinimumSpeedForCheck){
        if(scancount >= 15){
            if(checkControllerComms() == 2 || checkControllerError() == 2 || checkControllerBusVoltage() == 2 || checkEncoderRotations() == 2 || checkMotorTemperature() == 2 || checkMotorCurrent() == 2 || checkPDPCurrent() == 2)
            {
            return 2;
            }
            else if(checkControllerComms() == 1 || checkControllerError() == 1 || checkControllerBusVoltage() == 1 || checkEncoderRotations() == 1 || checkMotorTemperature() == 1 || checkMotorCurrent() == 1 || checkPDPCurrent() == 1)
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
        talonFXMotorCheck();
        if(Math.abs(m_talon.get()) >= kMinimumSpeedForCheck){
        scancount++;
        }
    }   

    private int checkControllerComms() {
        if (m_talon.getDeviceID() < 0) {
            return 2;
        } else {
            return 0;
        }
    }
//FIXME
    private int checkControllerError() {
        Faults faults = null;
        m_talon.getFaults(faults);
        if (faults.hasAnyFault()) {
            return 2;
        } else {
            return 0;
        }

    }

    private int checkControllerBusVoltage() {
        if (m_talon.getBusVoltage() < kControllerBusVoltageShutdown) {
            return 2;
        } else if (m_talon.getBusVoltage() < kControllerBusVoltageWarning
                && m_talon.getBusVoltage() >= kControllerBusVoltageShutdown) {
            return 1;
        } else {
            return 0;
        }
    }
    //FIXME
    // MAKE SURE TO TEST
    private int checkMotorCurrent() {
        if (m_talon.getSupplyCurrent() < kMotorOutputCurrentShutdown) {
            return 2;
        } else if (m_talon.getSupplyCurrent() < kMotorOutputCurrentWarning
                && m_talon.getSupplyCurrent() >= kMotorOutputCurrentShutdown) {
            return 1;
        } else {
            return 0;
        }

    } 
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
        if (Math.sqrt(Math.abs(m_talon.getActiveTrajectoryVelocity())) <= kEncoderVelocityShutdown) {
            return 2;
        } else if (Math.abs(m_talon.get()) <= kEncoderVelocityWarning && Math.abs(m_talon.getActiveTrajectoryVelocity()) > kEncoderVelocityShutdown) {
            return 1;
        } else {
            return 0;
        }
    }

    public int checkMotorTemperature() {
        if (m_talon.getTemperature() >= kMotorTemperatureShutdown) {
            return 2;
        } else if (m_talon.getTemperature() >= kMotorTemperatureWarning
                && m_talon.getTemperature() < kMotorTemperatureShutdown) {
            return 1;
        } else {
            return 0;
        }
    }


    public final int kMotorTemperatureShutdown = 80;
    public final int kMotorTemperatureWarning = 55;
    public final int kPDPOutputCurrentShutdown = 20;
    public final int kPDPOutputCurrentWarning = 25;
    public final int kMotorOutputCurrentShutdown = 20;
    public final int kMotorOutputCurrentWarning = 25;
    public final int kControllerBusVoltageShutdown = 10;
    public final int kControllerBusVoltageWarning = 12;
    public final int kEncoderVelocityShutdown = 0;
    public final int kEncoderVelocityWarning = 5;
    public final double kMinimumSpeedForCheck = 0.1;

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