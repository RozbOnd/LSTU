#include <vector>

class Lagrange {
    std::vector<double>polynom;
    std::vector<double>x,y;
    int n;
public:
    Lagrange(std::vector<double>n_x, std::vector<double>n_y) : x(n_x), y(n_y), n(x.size()) {
        polynom.resize(n, 0);
        buildPolynom();
    }

    void buildPolynom() {
        for (int i = 0; i < n; i++) {
            if (y[i] == 0) continue;

            double mul = y[i];
            for (int j = 0; j < n; j++) {
                if (j != i) mul /= (x[i] - x[j]);
            }

            std::vector<double>cur(2);
            cur[1] = 1;
            cur[0] = -(i == 0 ? x[1] : x[0]);
            for (int j = 0; j < n; j++) {
                if (i == j) continue;
                if (i == 0 && j == 1) continue;
                if (i >= 1 && j == 0) continue;
                std::vector<double>next(cur.size() + 1, 0);
                next[0] = -1 * cur[0] * x[j];
                for (int k = 1; k < next.size() - 1; k++) {
                    next[k] = cur[k - 1] - x[j] * cur[k];
                }
                next[next.size() - 1] = cur[cur.size() - 1];
                cur = next;
            }
            for (int j = 0; j < n; j++) {
                polynom[j] += cur[j] * mul;
            }
        }
    }

    std::vector<double> getPolynom() {
        return polynom;
    }

    double getValueInPoint(double find_x) {
        double value = 0;
        for (int i = n - 1; i >= 0; i--) {
            value = value * find_x + polynom[i];
        }
        return value;
    }
};