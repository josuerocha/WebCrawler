
M = csvread('termdata.csv');


xdata = [];
ydata = [];
zdata = [];


for i = 1 : size(M,1)

    xdata(end+1) = M(i,1);
    ydata(end+1) = M(i,2);
    zdata(end+1) = M(i,3);
end



disp('OPA ')
disp(size(xdata))
disp(size(ydata))
%% Plot

plot(xdata,ydata,'LineWidth',3)
title('Termos x Frequencia')
xlabel('Indice do termo (em centenas de milhares)')
ylabel('Frequência (em centenas de milhares)')
figure(2)

title('Termos x IDF')
scatter(xdata,zdata,'+')
xlabel('Indice do termo (em centenas de milhares)')
ylabel('IDF')