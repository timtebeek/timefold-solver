package ai.timefold.solver.core.impl.heuristic.selector.move.generic.chained;

import java.util.Objects;

import ai.timefold.solver.core.api.domain.solution.PlanningSolution;
import ai.timefold.solver.core.api.score.director.ScoreDirector;
import ai.timefold.solver.core.impl.domain.variable.descriptor.GenuineVariableDescriptor;
import ai.timefold.solver.core.impl.domain.variable.inverserelation.SingletonInverseVariableSupply;
import ai.timefold.solver.core.impl.heuristic.selector.move.generic.ChangeMove;
import ai.timefold.solver.core.impl.score.director.VariableDescriptorAwareScoreDirector;

/**
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 */
public class ChainedChangeMove<Solution_> extends ChangeMove<Solution_> {

    protected final Object oldTrailingEntity;
    protected final Object newTrailingEntity;

    public ChainedChangeMove(GenuineVariableDescriptor<Solution_> variableDescriptor, Object entity, Object toPlanningValue,
            SingletonInverseVariableSupply inverseVariableSupply) {
        super(variableDescriptor, entity, toPlanningValue);
        oldTrailingEntity = inverseVariableSupply.getInverseSingleton(entity);
        newTrailingEntity = toPlanningValue == null ? null
                : inverseVariableSupply.getInverseSingleton(toPlanningValue);
    }

    public ChainedChangeMove(GenuineVariableDescriptor<Solution_> variableDescriptor, Object entity, Object toPlanningValue,
            Object oldTrailingEntity, Object newTrailingEntity) {
        super(variableDescriptor, entity, toPlanningValue);
        this.oldTrailingEntity = oldTrailingEntity;
        this.newTrailingEntity = newTrailingEntity;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public boolean isMoveDoable(ScoreDirector<Solution_> scoreDirector) {
        return super.isMoveDoable(scoreDirector)
                && !Objects.equals(entity, toPlanningValue);
    }

    @Override
    protected void doMoveOnGenuineVariables(ScoreDirector<Solution_> scoreDirector) {
        var castScoreDirector = (VariableDescriptorAwareScoreDirector<Solution_>) scoreDirector;
        var oldValue = variableDescriptor.getValue(entity);
        // Close the old chain
        if (oldTrailingEntity != null) {
            castScoreDirector.changeVariableFacade(variableDescriptor, oldTrailingEntity, oldValue);
        }
        // Change the entity
        castScoreDirector.changeVariableFacade(variableDescriptor, entity, toPlanningValue);
        // Reroute the new chain
        if (newTrailingEntity != null) {
            castScoreDirector.changeVariableFacade(variableDescriptor, newTrailingEntity, entity);
        }
    }

    @Override
    public ChainedChangeMove<Solution_> rebase(ScoreDirector<Solution_> destinationScoreDirector) {
        return new ChainedChangeMove<>(variableDescriptor,
                destinationScoreDirector.lookUpWorkingObject(entity),
                destinationScoreDirector.lookUpWorkingObject(toPlanningValue),
                destinationScoreDirector.lookUpWorkingObject(oldTrailingEntity),
                destinationScoreDirector.lookUpWorkingObject(newTrailingEntity));
    }

}
