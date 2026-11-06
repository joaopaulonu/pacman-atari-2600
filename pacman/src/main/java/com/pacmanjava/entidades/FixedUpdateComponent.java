package com.pacmanjava.entidades;

import com.almasb.fxgl.entity.component.Component;

//Componente de Entity que contém uma função de 
//FixedUpdate que só é executada n/s vezes fixa
//permite manejar cálculos de colisão sém causar inconsisténcias em fps maiores ou menores
public class FixedUpdateComponent extends Component {
    private static final double FIXED_TIME_STEP = 0.01;

    private double timer = 0;
    private EntidadeJogo entity;

    public FixedUpdateComponent(EntidadeJogo entity) {
        this.entity = entity;
    }

    //timer guarda o tempo em segundos
    //se o timer chega a ser muito alto (por causa do congelamento da tela),
    //entra num loop de execução do onFixedUpdate até o timer diminuir de volta
    @Override
    public void onUpdate(double tpf) {
        timer += tpf;

        while (timer >= FIXED_TIME_STEP) {
            onFixedUpdate(FIXED_TIME_STEP);
            timer -= FIXED_TIME_STEP;
        }
    }

    public void onFixedUpdate(double timeStep) {
        if (entity != null) {
            entity.handleMovement(timeStep);
        }
    }
}