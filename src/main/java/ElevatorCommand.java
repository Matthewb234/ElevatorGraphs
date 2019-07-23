import com.team2073.common.command.AbstractLoggingCommand;
import edu.wpi.first.wpilibj.command.Command;

public class ElevatorCommand extends AbstractLoggingCommand {

    @Override
    protected boolean isFinishedDelegate() {
        return false;
    }


    @Override
    protected void executeDelegate() {
        Robot.getElevatorSubsystem().setElevatorActive(true);
    }


    @Override
    protected void endDelegate() {
        Robot.getElevatorSubsystem().setElevatorActive(false);
    }

}
