
%% Defining values
threads = [10 30 50 70 90 100];
%Average
avgTime = [100.5 53.25 54.5 48.5 54.5 68.25];

%Test 1
test1Time = [92 58 50 50 51 58];

%Test 2
test2Time = [107 54 57 46 58 50];

%Test 3
test3Time = [98 53 54 54 50 56];

%Test 4
test4Time = [105 48 57 44 59 57];

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
ylabel('Tempo de execução (segundos)');
grid
title('Tempo de execução relacionado à quantidade de threads');