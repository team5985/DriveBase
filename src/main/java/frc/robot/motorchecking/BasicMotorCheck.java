package frc.robot.motorchecking;

import com.revrobotics.CANEncoder;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.Spark;

public class BasicMotorCheck extends MotorCheck {
    PowerDistributionPanel m_PDP;
    int m_controllerChannel;
    Spark m_spark;
    int faultScanCount;
    boolean faultOnLastScan;
    int scancount;
    CANEncoder m_CANencoder;
    Encoder m_encoder;
    double lastEncoderValue;
    double encoderDifference;
    
     /**
      * At current moment, javadocs aren't complete-
        First encoder - default encoder
        Second Encoder - CANENCODER
      */
    public BasicMotorCheck(Spark spark, int controllerChannel, PowerDistributionPanel pdp,  Encoder encoder , CANEncoder canencoder) {
        m_spark = spark;
        m_controllerChannel = controllerChannel;
        m_PDP = pdp;
        m_encoder = encoder;
        m_CANencoder = canencoder;
        scancount = 0;
        faultScanCount = 0;
        faultOnLastScan = false;
        lastEncoderValue = 0;
        encoderDifference = 0;
    }
    public void update() {
        myStatus = checkMotor();
        if(Math.abs(m_spark.get()) >= motorCheckConstants.kMinimumSpeedForCheck){
        scancount++;
        }
    }   

    private int myStatus = 0;

    public int getStatus()
    {
        return myStatus;
    }



    private int checkMotor(){
    if(Math.abs(m_spark.get()) >= motorCheckConstants.kMinimumSpeedForCheck){
        if(scancount >= 15){
            if(checkPDPCurrent() == 2 || checkFirstEncoderRotations() == 2 || checkSecondEncoderRotations() == 2)
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
            else if(checkPDPCurrent() == 1 || checkFirstEncoderRotations() == 1 || checkSecondEncoderRotations() == 1)
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
            faultOnLastScan = false;
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

    private int checkFirstEncoderRotations() {
        if (m_encoder == null){
        return 0;
        }
        else{
            encoderDifference = Math.abs(m_encoder.get() - lastEncoderValue);
            lastEncoderValue = m_encoder.get();
            if (encoderDifference <= motorCheckConstants.kEncoderVelocityShutdown) {
                return 2;
            } else if (encoderDifference <= motorCheckConstants.kEncoderVelocityWarning) {
                return 1;
            } else {
                return 0;
            }
        }
    }
    private int checkSecondEncoderRotations() {
        if(m_CANencoder == null){
            return 0;
        }
        else{
            if (Math.sqrt(Math.abs(m_CANencoder.getVelocity())) <= motorCheckConstants.kEncoderVelocityShutdown) {
                return 2;
            } else if (Math.abs(m_CANencoder.getVelocity()) <= motorCheckConstants.kEncoderVelocityWarning && Math.abs(m_CANencoder.getVelocity()) > motorCheckConstants.kEncoderVelocityShutdown) {
                return 1;
            } else {
                return 0;
            }
        }
    }


}

