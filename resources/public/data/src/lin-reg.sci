
// Initialise data

data = csvRead('ex1data2.txt');
X = data(:, 1:2);
y = data(:, 3);
m = length(y)

// normalize the data

mu = mean(X, "r");
sigma = stdev(X, "r");

for i = 1:size(X, "c")
    X(:,i) = (X(:,i) - mu(i)) ./ sigma(i);
end;

// add x_0 to X

X = [ones(m, 1) X];

// set parameters for iterations and
// learning rate

alpha = 0.01;
num_iters = 400;
theta = zeros(3, 1);
J_history = zeros(num_iters, 1);

// run the gradientDescent

for iter = 1:num_iters
    hypothesis = X * theta;
    errors = hypothesis - y;
    change = (alpha * (X' * errors)) / m;
    theta = theta - change;
    
    J_history(iter) = (sum(((X * theta) - y) .^ 2)) / (2 * m);
end;

// plot the convergence graph

plot(1:size(J_history, "r"), J_history, '-g', 'LineWidth', 1);
xtitle("Convergence of Cost Function", "Iterations", "Cost");



//  Estimate the price of a 1650 sq-ft, 3 br house

norm_area = (1650 - mu(1)) / sigma(1);
norm_bedrooms = (3 - mu(2)) / sigma(2);
price = theta(1) + (theta(2) * norm_area) + (theta(3) * norm_bedrooms);

