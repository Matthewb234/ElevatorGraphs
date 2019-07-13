
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.team2073.common.speedcontroller.EagleSPX;
import com.team2073.common.speedcontroller.EagleSRX;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Solenoid;


public class Elevator {

    private EagleSRX elevatorMaster;
    private EagleSPX elevatorSlave;
    private Solenoid bikeBrakeSolenoid;
    private DigitalInput topSensor;
    private DigitalInput bottomSensor;

    public void setSlave() {

        elevatorSlave.follow(elevatorMaster);
    }

    public void init() {
        configEncoder();
        setSlave();
    }

    public void configEncoder() {
        elevatorMaster.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, 10);
        elevatorMaster.configAllowableClosedloopError(0, 0, 10);
        elevatorMaster.setInverted(Elevator.MASTER_DEFAULT_DIRECTION);
        elevatorSlave.setInverted(Elevator.SLAVE_DEFAULT_DIRECTION);
    }

}
