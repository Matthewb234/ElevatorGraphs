
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.team2073.common.speedcontroller.EagleSPX;
import com.team2073.common.speedcontroller.EagleSRX;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.command.Subsystem;


public class ElevatorSubsystem extends Subsystem {

    private EagleSRX elevatorMaster;
    private EagleSPX elevatorSlave;
    private Solenoid bikeBrakeSolenoid;
    private Joystick controller;
    private boolean elevatorActive;

    public ElevatorSubsystem(Joystick controller){
        this.controller = controller;
    }

    public void setOutput(){
        elevatorMaster.set(ControlMode.PercentOutput, .1);
    }

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
//        elevatorMaster.setInverted(Elevator.MASTER_DEFAULT_DIRECTION);
//        elevatorSlave.setInverted(Elevator.SLAVE_DEFAULT_DIRECTION);

    }

    @Override
    public void periodic() {
        setOutput();
    }

    @Override
    protected void initDefaultCommand() {

    }
    public void setElevatorActive(boolean elevatorActive){
        this.elevatorActive = elevatorActive;
    }
}
