#include <iostream>
#include <iomanip>
#include <vector>
#include <string>
#include <sstream>
#include <stdexcept>
#include <cmath>
#include <limits>
#include <algorithm>
#include <locale>

#include "Lagrange.hpp"
#include "Newton.hpp"
#include "Aitken.hpp"

using std::cin;
using std::cout;
using std::string;
using std::vector;

// ---------- Ввод с проверкой ----------

double readDouble(const string& prompt) {
    while (true) {
        cout << prompt;

        string s;
        std::getline(cin, s);

        if (!cin) {
            throw std::runtime_error("Ввод был прерван.");
        }

        std::stringstream ss(s);
        double value;
        char extra;

        if ((ss >> value) && !(ss >> extra) && std::isfinite(value)) {
            return value;
        }

        cout << "Ошибка: введите корректное числовое значение.\n";
    }
}

int readInt(const string& prompt, int minValue, int maxValue) {
    while (true) {
        cout << prompt;

        string s;
        std::getline(cin, s);

        if (!cin) {
            throw std::runtime_error("Ввод был прерван.");
        }

        std::stringstream ss(s);
        int value;
        char extra;

        if ((ss >> value) && !(ss >> extra) &&
            value >= minValue && value <= maxValue) {
            return value;
        }

        cout << "Ошибка: введите целое число от "
             << minValue << " до " << maxValue << ".\n";
    }
}

// ---------- Форматирование многочленов ----------

string num(double value, int precision = 6) {
    if (std::abs(value) < 1e-12) {
        value = 0.0;
    }

    std::ostringstream out;
    out << value;
    return out.str();
}

string variablePower(int degree) {
    if (degree == 0) return "";
    if (degree == 1) return "x";
    return "x^" + std::to_string(degree);
}

// Многочлен по коэффициентам в порядке возрастания степеней:
// c[0] + c[1]x + ... + c[n]x^n.
// Выводится по убыванию степеней.
string polynomialFromCoefficients(const vector<double>& c) {
    string result;
    const double EPS = 1e-6;

    for (int i = static_cast<int>(c.size()) - 1; i >= 0; --i) {
        if (std::abs(c[i]) < EPS) continue;

        double a = c[i];
        bool negative = a < 0;
        double absA = std::abs(a);

        if (result.empty()) {
            if (negative) result += "-";
        } else {
            result += negative ? " - " : " + ";
        }

        if (i == 0) {
            result += num(absA);
        } else {
            if (std::abs(absA - 1.0) > EPS) {
                result += num(absA) + "*";
            }
            result += variablePower(i);
        }
    }

    return result.empty() ? "0" : result;
}

// ---------- Построение строк для исходных форм ----------

string lagrangeOriginal(const vector<double>& x, const vector<double>& y) {
    std::ostringstream out;

    for (size_t i = 0; i < x.size(); ++i) {
        if (i > 0) out << " + ";

        out << "(" << num(y[i]) << ")*(";

        bool first = true;
        for (size_t j = 0; j < x.size(); ++j) {
            if (j == i) continue;

            if (!first) out << "*";
            out << "(x - (" << num(x[j]) << "))";
            first = false;
        }

        out << ")/(";
        double denominator = 1.0;
        for (size_t j = 0; j < x.size(); ++j) {
            if (j != i) {
                denominator *= (x[i] - x[j]);
            }
        }
        out << num(denominator) << ")";
    }

    return out.str();
}

string newtonOriginal(const vector<double>& x,
                      const vector<vector<double>>& rr) {
    std::ostringstream out;

    out << num(rr[0][0]);

    for (size_t i = 1; i < x.size(); ++i) {
        out << " + (" << num(rr[i][0]) << ")*";

        for (size_t j = 0; j < i; ++j) {
            out << "(x - (" << num(x[j]) << "))";
        }
    }

    return out.str();
}

// ---------- Вспомогательные операции для вывода Эйткена ----------

void printAitkenTable(const vector<vector<double>>& table) {
    cout << "\nПромежуточные значения схемы Эйткена:\n";

    for (size_t k = 0; k < table.size(); ++k) {
        cout << "P[" << k << "]: ";

        for (size_t j = 0; j < table[k].size(); ++j) {
            cout << "P(" << j << "," << j + k << ") = "
                 << num(table[k][j]);

            if (j + 1 < table[k].size()) {
                cout << "    ";
            }
        }

        cout << '\n';
    }
}

// ---------- Вывод разделённых разностей ----------

void printDividedDifferences(const vector<vector<double>>& rr) {
    cout << "\nРазделённые разности для многочлена Ньютона:\n";

    for (size_t i = 0; i < rr.size(); ++i) {
        cout << "Порядок " << i << ": ";

        for (size_t j = 0; j < rr[i].size(); ++j) {
            cout << num(rr[i][j]);

            if (j + 1 < rr[i].size()) {
                cout << "    ";
            }
        }

        cout << '\n';
    }
}

// ---------- Основная программа ----------

int main() {
    std::setlocale(LC_ALL, "ru_RU.UTF-8");
    try {
        cout << "===============================================\n";
        cout << " Интерполяция Лагранжа, Ньютона и схема Эйткена\n";
        cout << "===============================================\n\n";

        const int n = readInt(
            "Введите количество узлов интерполяции (n >= 2): ", 2, 100);

        vector<double> x(n);
        vector<double> y(n);

        cout << "\nВведите узлы таблицы в порядке возрастания.\n";

        for (int i = 0; i < n; ++i) {
            x[i] = readDouble("x[" + std::to_string(i) + "] = ");

            if (i > 0 && x[i] <= x[i - 1]) {
                throw std::invalid_argument(
                    "Узлы должны быть строго упорядочены по возрастанию.");
            }

            y[i] = readDouble("y[" + std::to_string(i) + "] = ");
        }

        const double xStar = readDouble("\nВведите точку x* = ");

        // Точка должна находиться внутри отрезка интерполяции.
        if (xStar < x.front() || xStar > x.back()) {
            throw std::out_of_range(
                "Точка x* находится вне отрезка интерполяции "
                "[x0, xn-1].");
        }

        // Создание объектов уже реализованных алгоритмов.
        Lagrange lagrange(x, y);
        Newton newton(x, y);
        Aitken aitken(x, y);

        // ---------- Лагранж ----------
        const vector<double> lagrangePoly = lagrange.getPolynom();

        cout << "\n\n========== МНОГОЧЛЕН ЛАГРАНЖА ==========\n";

        cout << "\nИсходный вид:\n";
        cout << "L(x) = " << lagrangeOriginal(x, y) << '\n';

        cout << "\nУпрощённый вид (по убыванию степеней):\n";
        cout << "L(x) = " << polynomialFromCoefficients(lagrangePoly)
             << '\n';

        const double lagrangeValue = lagrange.getValueInPoint(xStar);

        // ---------- Ньютон ----------
        const vector<double> newtonPoly = newton.getPolynom();
        const vector<vector<double>> rr = newton.getRR();

        cout << "\n\n========== МНОГОЧЛЕН НЬЮТОНА ==========\n";

        cout << "\nИсходный вид:\n";
        cout << "N(x) = " << newtonOriginal(x, rr) << '\n';

        cout << "\nУпрощённый вид (по убыванию степеней):\n";
        cout << "N(x) = " << polynomialFromCoefficients(newtonPoly)
             << '\n';

        printDividedDifferences(rr);

        const double newtonValue = newton.getValueInPoint(xStar);

        // ---------- Эйткен ----------
        const double aitkenValue = aitken.getValueInPoint(xStar);

        cout << "\n\n========== СХЕМА ЭЙТКЕНА ==========\n";

        const vector<vector<double>> aitkenTable =
            aitken.buildTable(xStar);

        printAitkenTable(aitkenTable);

        cout << "\nЗначение по схеме Эйткена:\n";
        cout << "P(x*) = " << num(aitkenValue) << '\n';

        // ---------- Сравнение ----------
        cout << "\n\n========== РЕЗУЛЬТАТЫ ==========\n";

        cout << "x* = " << num(xStar) << "\n\n";

        cout << "Лагранж : " << num(lagrangeValue) << '\n';
        cout << "Ньютон  : " << num(newtonValue) << '\n';
        cout << "Эйткен  : " << num(aitkenValue) << '\n';

        cout << "\nАбсолютные разности:\n";
        cout << "|Лагранж - Ньютон| = "
             << num(std::abs(lagrangeValue - newtonValue)) << '\n';

        cout << "|Лагранж - Эйткен| = "
             << num(std::abs(lagrangeValue - aitkenValue)) << '\n';

        cout << "|Ньютон - Эйткен|  = "
             << num(std::abs(newtonValue - aitkenValue)) << '\n';

        cout << "\nПрограмма завершена успешно.\n";
    }
    catch (const std::invalid_argument& e) {
        cout << "\nОшибка ввода/узлов: " << e.what() << '\n';
        return 1;
    }
    catch (const std::out_of_range& e) {
        cout << "\nОшибка диапазона: " << e.what() << '\n';
        return 2;
    }
    catch (const std::exception& e) {
        cout << "\nОшибка: " << e.what() << '\n';
        return 3;
    }
    catch (...) {
        cout << "\nНеизвестная ошибка.\n";
        return 4;
    }

    return 0;
}
