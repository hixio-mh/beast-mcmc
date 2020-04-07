package dr.inference.hmc;

import dr.math.matrixAlgebra.WrappedVector;
/**
 * @author Zhenyu Zhang
 */

public interface ReversibleHMCProvider {

    void reversiblePositionUpdate(WrappedVector position, WrappedVector momentum, int direction, double time);

    WrappedVector drawMomentum();
}
