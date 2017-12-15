
%% Defining values
threads = [10 30 50 70 90 100];
%Average
avgTime = [118.5 90.75 78 77.52 76 67.5];
avgTime = 500 ./ avgTime;

%Test 1
test1Time = [107 87 70 58 82 62];
test1Time = 500 ./ test1Time;

%Test 2
test2Time = [121 91 84 80 72 62];
test2Time = 500 ./ test2Time;

%Test 3
test3Time = [109 102 82 93 76 70];
test3Time = 500 ./ test3Time;

%Test 4
test4Time = [137 83 76 78 74 76];
test4Time = 500 ./ test4Time;

%% Plotting
plot(threads,test1Time);
hold on
plot(threads,test2Time);
hold on
plot(threads,test3Time);
hold on
plot(threads,test4Time);
hold on
plot(threads,avgTime,'LineWidth',3);

legend('Teste 1','Teste 2','Teste 3','Teste 4','Média aritmética');
xlabel('Número de threads');
ylabel('Páginas por segundo');
grid
title('Tempo de execução relacionado à quantidade de threads');