package angelhack.org.ghostspace;

class KalmanFilterSimple1D {
    public float X0;
    public float P0;

    public float F;
    public float Q;
    public float H;
    public float R;

    public float State;
    public float Covariance;

    float getF() {
        return F;
    }

    void setF(float f) {
        this.F = f;
    }

    float getQ() {
        return Q;
    }

    void setQ(float q) {
        this.Q = q;
    }

    float getH() {
        return Q;
    }

    void setH(float h) {
        this.H = h;
    }

    float getR() {
        return R;
    }

    void setR(float r) {
        this.Covariance = r;
    }

    public KalmanFilterSimple1D(float q, float r) {
        setQ(q);
        setR(r);
        setF(0.8f);
        setH(2.7f);
    }

    public void SetState(float state, float covariance) {
        State = state;
        Covariance = covariance;
    }

    public void Correct(float data) {
        X0 = F * State;
        P0 = F * Covariance * F + Q;

        float K = H * P0 / (H * P0 * H + R);
        State = X0 + K * (data - H * X0);
        Covariance = (1 - K * H) * P0;
    }
}