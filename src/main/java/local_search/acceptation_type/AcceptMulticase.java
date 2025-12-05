package local_search.acceptation_type;

import local_search.complement.MultiCaseSimulatedAnnealing;
import metaheuristics.generators.*;
import metaheuristics.strategy.*;

import java.util.List;
import java.util.Random;

import problem.definition.State;

/**
 * @class AcceptMulticase
 * @brief Implementa la lógica de aceptación de candidatos para el algoritmo de Recocido Simulado Multicaso.
 * 
 * Esta clase extiende de AcceptableCandidate y define las reglas para aceptar o rechazar
 * una solución candidata basándose en criterios de dominancia y probabilidad.
 */
public class AcceptMulticase extends AcceptableCandidate {

	/**
	 * @brief Generador de números aleatorios compartido.
	 * Se utiliza una instancia estática para mejorar la eficiencia y la calidad de la aleatoriedad.
	 */
	private static final Random rdm = new Random();

	/**
	 * @brief Determina si se acepta una solución candidata.
	 * 
	 * Evalúa la solución candidata frente a la solución actual utilizando criterios de dominancia
	 * y una función de probabilidad basada en la temperatura actual del recocido simulado.
	 * 
	 * @param stateCurrent Estado actual de la solución.
	 * @param stateCandidate Estado candidato a ser evaluado.
	 * @return true si el candidato es aceptado, false en caso contrario.
	 */
	@Override
	public Boolean acceptCandidate(State stateCurrent, State stateCandidate) {
		// TODO Auto-generated method stub
		Boolean accept = false;
		List<State> list = Strategy.getStrategy().listRefPoblacFinal;
		
		if(list.size() == 0){
			list.add(stateCurrent.clone());
		}
		Double T = MultiCaseSimulatedAnnealing.tinitial;
		double pAccept = 0;
		Dominance dominance= new Dominance();
		//Verificando si la soluci�n candidata domina a la soluci�n actual
		//Si la soluci�n candidata domina a la soluci�n actual
		if(dominance.dominance(stateCandidate, stateCurrent) == true){
			//Se asigna como soluci�n actual la soluci�n candidata con probabilidad 1
			pAccept = 1; 
		}
		else if(dominance.dominance(stateCandidate, stateCurrent)== false){	
			if(DominanceCounter(stateCandidate, list) > 0){
				pAccept = 1;
			}
			else if(DominanceRank(stateCandidate, list) == 0){
				pAccept = 1;
			}
			else if(DominanceRank(stateCandidate, list) < DominanceRank(stateCurrent, list)){
				pAccept = 1;
			}
			else if(DominanceRank(stateCandidate, list) == DominanceRank(stateCurrent, list)){
				//Calculando la probabilidad de aceptaci�n
				List<Double> evaluations = stateCurrent.getEvaluation();
				double total = 0;
				for (int i = 0; i < evaluations.size()-1; i++) {
					Double evalA = evaluations.get(i);
					Double evalB = stateCandidate.getEvaluation().get(i);
					if (evalA != 0 && evalB != 0) {
						total += (evalA - evalB)/((evalA + evalB)/2);
					}
				}	
				pAccept = Math.exp(-(1-total)/T);
			}
			else if (DominanceRank(stateCandidate, list) > DominanceRank(stateCurrent, list) && DominanceRank(stateCurrent, list)!= 0){
				float value = (float) DominanceRank(stateCandidate, list)/DominanceRank(stateCurrent, list);
				pAccept = Math.exp(-(value+1)/T);
			}
			else{
				//Calculando la probabilidad de aceptaci�n
				List<Double> evaluations = stateCurrent.getEvaluation();
				double total = 0;
				for (int i = 0; i < evaluations.size()-1; i++) {
					Double evalA = evaluations.get(i);
					Double evalB = stateCandidate.getEvaluation().get(i);
					if (evalA != 0 && evalB != 0) {
						total += (evalA - evalB)/((evalA + evalB)/2);
					}
				}
				pAccept = Math.exp(-(1-total)/T);
			}
		}
		//Generar un n�mero aleatorio
		if((rdm.nextFloat()) < pAccept){
			stateCurrent = stateCandidate.clone();
			//Verificando que la soluci�n candidata domina a alguna de las soluciones
			accept = dominance.ListDominance(stateCandidate, list);
		}
		return accept;
	}


	/**
	 * @brief Cuenta cuántas soluciones en la lista son dominadas por el candidato.
	 * 
	 * @param stateCandidate Estado candidato a evaluar.
	 * @param list Lista de soluciones de referencia (ej. frente de Pareto actual).
	 * @return Número de soluciones en la lista que son dominadas por el candidato.
	 */
	private int DominanceCounter(State stateCandidate, List<State> list) { //chequea el nmero de soluciones de Pareto que son dominados por la nueva solucin
		int counter = 0;
		for (int i = 0; i < list.size(); i++) {
			State solution = list.get(i);
			Dominance dominance = new Dominance();
			if(dominance.dominance(stateCandidate, solution) == true)
				counter++;
		}
		return counter;
	}

	/**
	 * @brief Calcula el rango de dominancia del candidato.
	 * 
	 * Cuenta cuántas soluciones en la lista dominan al candidato.
	 * 
	 * @param stateCandidate Estado candidato a evaluar.
	 * @param list Lista de soluciones de referencia.
	 * @return Número de soluciones en la lista que dominan al candidato.
	 */
	private int DominanceRank(State stateCandidate, List<State> list) { //calculando el nmero de soluciones en el conjunto de Pareto que dominan a la solucin
		int rank = 0;
		for (int i = 0; i < list.size(); i++) {
			State solution = list.get(i);
			Dominance dominance = new Dominance();
			if(dominance.dominance(solution, stateCandidate) == true){
				rank++;
			}
		}
		
		return rank;
	}

}
