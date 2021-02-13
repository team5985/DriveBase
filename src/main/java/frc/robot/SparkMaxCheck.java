package frc.robot;

import com.revrobotics.CANEncoder;
import com.revrobotics.CANSparkMax;
import com.revrobotics.SparkMax;

import edu.wpi.first.wpilibj.PowerDistributionPanel;

public class SparkMaxCheck {
    CANSparkMax m_spark;
    PowerDistributionPanel m_PDP;
    int m_controllerChannel;
    CANEncoder m_encoder;
    int scancount;

    public SparkMaxCheck(CANSparkMax spark, CANEncoder encoder, int controllerChannel, PowerDistributionPanel pdp) {
        m_spark = spark;
        m_encoder = encoder;
        m_controllerChannel = controllerChannel;
        m_PDP = pdp;
        scancount = 0;
    }

    /*
     * Comms with m_spark max CANSPARKMax.class line 819???? Errors reported by
     * m_spark max CANSPARKMax.class Line 90 Bus Voltage as detected by sparkmax
     * CANSPARKMax.class line 705 Motor current as detected by m_spark max
     * CANSPARKMax.class line 719 Current as detected by m_PDP Motor Rotations on
     * Encoder CANSPARKMax.class Line 257 Motor Tempertaure CANSPARKMax.class Line
     * 726
     * 
     * CHECK IF ENCODER CONNECTED;;; //FIXME 
     */
    public int sparkMaxMotorCheck(){

    if(Math.abs(m_spark.get()) >= kMinimumSpeedForCheck){
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
        sparkMaxMotorCheck();
        if(Math.abs(m_spark.get()) >= kMinimumSpeedForCheck){
        scancount++;
        }
    }   

    private int checkControllerComms() {
        if (m_spark.getDeviceId() < 0) {
            return 2;
        } else {
            return 0;
        }
    }

    private int checkControllerError() {
        if (m_spark.getFaults() > 0) {
            return 2;
        } else {
            return 0;
        }

    }

    private int checkControllerBusVoltage() {
        if (m_spark.getBusVoltage() < kControllerBusVoltageShutdown) {
            return 2;
        } else if (m_spark.getBusVoltage() < kControllerBusVoltageWarning
                && m_spark.getBusVoltage() >= kControllerBusVoltageShutdown) {
            return 1;
        } else {
            return 0;
        }
    }
    //FIXME
    private int checkMotorCurrent() {
        if (m_spark.getOutputCurrent() < kMotorOutputCurrentShutdown) {
            return 2;
        } else if (m_spark.getOutputCurrent() < kMotorOutputCurrentWarning
                && m_spark.getOutputCurrent() >= kMotorOutputCurrentShutdown) {
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
        if (Math.sqrt(Math.abs(m_encoder.getVelocity())) <= kEncoderVelocityShutdown) {
            return 2;
        } else if (Math.abs(m_encoder.getVelocity()) <= kEncoderVelocityWarning && Math.abs(m_encoder.getVelocity()) > kEncoderVelocityShutdown) {
            return 1;
        } else {
            return 0;
        }
    }

    private int checkMotorTemperature() {
        if (m_spark.getMotorTemperature() >= kMotorTemperatureShutdown) {
            return 2;
        } else if (m_spark.getMotorTemperature() >= kMotorTemperatureWarning
                && m_spark.getMotorTemperature() < kMotorTemperatureShutdown) {
            return 1;
        } else {
            return 0;
        }
    }


    public final int kMotorTemperatureShutdown = 80;
    public final int kMotorTemperatureWarning = 55;
    public final int kPDPOutputCurrentShutdown = 4;
    public final int kPDPOutputCurrentWarning = 7;
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