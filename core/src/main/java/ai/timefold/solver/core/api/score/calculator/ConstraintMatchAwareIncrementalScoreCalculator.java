package ai.timefold.solver.core.api.score.calculator;

import java.util.Collection;
import java.util.Map;

import ai.timefold.solver.core.api.domain.solution.PlanningSolution;
import ai.timefold.solver.core.api.score.Score;
import ai.timefold.solver.core.api.score.ScoreExplanation;
import ai.timefold.solver.core.api.score.constraint.ConstraintMatchTotal;
import ai.timefold.solver.core.api.score.constraint.Indictment;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * Allows a {@link IncrementalScoreCalculator} to report {@link ConstraintMatchTotal}s
 * for explaining a score (= which score constraints match for how much)
 * and also for score corruption analysis.
 *
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 * @param <Score_> the {@link Score} type
 */
public interface ConstraintMatchAwareIncrementalScoreCalculator<Solution_, Score_ extends Score<Score_>>
        extends IncrementalScoreCalculator<Solution_, Score_> {

    /**
     * Allows for increased performance because it only tracks if constraintMatchEnabled is true.
     * <p>
     * Every implementation should call {@link #resetWorkingSolution}
     * and only handle the constraintMatchEnabled parameter specifically (or ignore it).
     *
     * @param workingSolution to pass to {@link #resetWorkingSolution}.
     * @param constraintMatchEnabled true if {@link #getConstraintMatchTotals()} or {@link #getIndictmentMap()} might be called.
     */
    void resetWorkingSolution(@NonNull Solution_ workingSolution, boolean constraintMatchEnabled);

    /**
     * @return never null;
     *         if a constraint is present in the problem but resulted in no matches,
     *         it should still be present with a {@link ConstraintMatchTotal#getConstraintMatchSet()} size of 0.
     * @throws IllegalStateException if {@link #resetWorkingSolution}'s constraintMatchEnabled parameter was false
     */
    @NonNull
    Collection<ConstraintMatchTotal<Score_>> getConstraintMatchTotals();

    /**
     * @return null if it should to be calculated non-incrementally from {@link #getConstraintMatchTotals()}
     * @throws IllegalStateException if {@link #resetWorkingSolution}'s constraintMatchEnabled parameter was false
     * @see ScoreExplanation#getIndictmentMap()
     */
    @Nullable
    Map<Object, Indictment<Score_>> getIndictmentMap();

}
