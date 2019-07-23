import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotState;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.buttons.JoystickButton;


public class Robot extends TimedRobot {
    Robot() {
        super(.01);
    }


    private TalonSRX talon = new TalonSRX(0);
    private static Joystick controller = new Joystick(0);
    private static ElevatorSubsystem elevatorSubsystem = new ElevatorSubsystem(controller);
    public static final int bikeBrakeSolenoid = 2;
    public static final int elevatorMaster = 5;
    public static final int elevatorSlave = 5;


    private JoystickButton x = new JoystickButton(controller, 1);


    private ElevatorCommand elevatorCommand = new ElevatorCommand();

    @Override
    public void robotInit() {
        x.toggleWhenPressed(elevatorCommand);
    }




    @Override
    public void robotPeriodic() {


    }


    @Override
    public void disabledPeriodic() {


    }


    @Override
    public void autonomousPeriodic() {


    }


    @Override
    public void teleopPeriodic() {


    }


    @Override
    public void testPeriodic() {
    }

    public static ElevatorSubsystem getElevatorSubsystem(){
        return elevatorSubsystem;
    }
}


