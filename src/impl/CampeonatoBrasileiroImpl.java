package impl;

import dominio.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CampeonatoBrasileiroImpl {

    private Map<Integer, List<Jogo>> brasileirao;
    private List<Jogo> jogos;
    private Predicate<Jogo> filtro;

    public CampeonatoBrasileiroImpl(Path arquivo, Predicate<Jogo> filtro) throws IOException {
        this.jogos = lerArquivo(arquivo);
        this.filtro = filtro;
        this.brasileirao = jogos.stream()
                .filter(filtro) //filtrar por ano
                .collect(Collectors.groupingBy(
                        Jogo::rodada,
                        Collectors.mapping(Function.identity(), Collectors.toList())));

    }

    public List<Jogo> lerArquivo(Path file) throws IOException {
        List<Jogo> todosOsJogos = new ArrayList<Jogo>();
        try {
            Stream<String> stream = Files.lines(file);
            List<String> dados = stream.toList();

            DateTimeFormatter formatterData = DateTimeFormatter.ofPattern("dd/MM/yyy");
            DateTimeFormatter formatterTime = DateTimeFormatter.ofPattern("HH'h'mm");
            DateTimeFormatter formatterTimePontos = DateTimeFormatter.ofPattern("HH':'mm");

            for(int i = 1; i < dados.size(); i++){
                String linha = dados.get(i);
                String[] valores = linha.split(";");

                Integer rodada = Integer.parseInt(valores[0]);
                LocalDate localDate = LocalDate.parse(valores[1], formatterData);
                LocalTime localTime = null;
                if(valores[2].contains("h")) {
                    localTime = LocalTime.parse(valores[2], formatterTime);
                } else if (valores[2].contains(":")) {
                    localTime = LocalTime.parse(valores[2], formatterTimePontos);
                }
                DataDoJogo data = new DataDoJogo(localDate, localTime, getDayOfWeek(valores[3]));
                Time timeMandante = new Time(valores[4]);
                Time timeVisitante = new Time(valores[5]);
                Time timeVencedor = new Time(valores[6]);
                String arena = valores[7];
                Integer placarMandante = Integer.parseInt(valores[8]);
                Integer placarVisitante = Integer.parseInt(valores[9]);
                String estadoMandante = valores[10];
                String estadoVisitante = valores[11];
                String estadoVencedor = valores[12];

                Jogo jogo = new Jogo(rodada, data, timeMandante, timeVisitante, timeVencedor, arena, placarMandante, placarVisitante, estadoMandante, estadoVisitante, estadoVencedor);
                todosOsJogos.add(jogo);
            }
            stream.close();
        } catch (IOException e) {
            System.out.println(e);
        }
        return todosOsJogos;
    }

    public IntSummaryStatistics getEstatisticasPorJogo() {

        List<Jogo> jogosBrasileirao = todosOsJogos();

        return jogosBrasileirao.stream()
                .mapToInt(Jogo::getTotalGols)
                .summaryStatistics();
    }

    public Map<Jogo, Integer> getMediaGolsPorJogo() {
        return null;
    }

    public IntSummaryStatistics GetEstatisticasPorJogo() {
        return null;
    }

    public List<Jogo> todosOsJogos() {
        return this.jogos.stream()
                .filter(this.filtro)
                .toList();
    }

    public Long getTotalVitoriasEmCasa() {
        return this.jogos.stream()
                .filter(this.filtro.and(jogo -> jogo.vencedor().equals(jogo.mandante())))
                .count();
    }

    public Long getTotalVitoriasForaDeCasa() {
        return this.jogos.stream()
                .filter(filtro.and(jogo -> jogo.vencedor().equals(jogo.visitante())))
                .count();
    }

    public Long getTotalEmpates() {
        return this.jogos.stream()
                .filter(filtro.and(jogo -> jogo.visitantePlacar().equals(jogo.mandantePlacar())))
                .count();
    }

    public Long getTotalJogosComMenosDe3Gols() {
        return this.jogos.stream()
                .filter(this.filtro.and(jogo -> (jogo.mandantePlacar() + jogo.visitantePlacar()) < 3))
                .count();
    }

    public Long getTotalJogosCom3OuMaisGols() {
        return this.jogos.stream()
                .filter(this.filtro.and(jogo -> (jogo.mandantePlacar() + jogo.visitantePlacar()) >= 3))
                .count();
    }

    public Map<Resultado, Long> getTodosOsPlacares() {
        return null;
    }

    public Map.Entry<Resultado, Long> getPlacarMaisRepetido() {
        Map<Resultado, Long> placarToQuantidade = this.jogos.stream()
                .filter(filtro) //filtrar por ano
                .collect(Collectors.groupingBy(
                        Jogo::getResutado,
                        Collectors.mapping(Function.identity(), Collectors.counting())));

        Map.Entry<Resultado, Long> maisRepetido = null;
        for (Map.Entry<Resultado, Long> placar : placarToQuantidade.entrySet()) {
            if (maisRepetido == null || placar.getValue().compareTo(maisRepetido.getValue()) > 0) {
                maisRepetido = placar;
            }
        }
        return maisRepetido;
    }

    public Map.Entry<Resultado, Long> getPlacarMenosRepetido() {
        Map<Resultado, Long> placarToQuantidade = this.jogos.stream()
                .filter(filtro) //filtrar por ano
                .collect(Collectors.groupingBy(
                        Jogo::getResutado,
                        Collectors.mapping(Function.identity(), Collectors.counting())));

        Map.Entry<Resultado, Long> menosRepetido = null;
        for (Map.Entry<Resultado, Long> placar : placarToQuantidade.entrySet()) {
            if (menosRepetido == null || menosRepetido.getValue().compareTo(placar.getValue()) > 0) {
                menosRepetido = placar;
            }
        }
        return menosRepetido;
    }

    private List<Time> getTodosOsTimes() {
        return null;
    }

    private Map<Time, List<Jogo>> getTodosOsJogosPorTimeComoMandantes() {
        return null;
    }

    private Map<Time, List<Jogo>> getTodosOsJogosPorTimeComoVisitante() {
        return null;
    }

    public Map<Time, List<Jogo>> getTodosOsJogosPorTime() {
        return null;
    }

    public Map<Time, Map<Boolean, List<Jogo>>> getJogosParticionadosPorMandanteTrueVisitanteFalse() {
        return null;
    }

    public Set<PosicaoTabela> getTabela() {
        return null;
    }

    private DayOfWeek getDayOfWeek(String dia) {
        return switch (dia) {
            case "Segunda-feira" -> DayOfWeek.MONDAY;
            case "Terça-feira" -> DayOfWeek.TUESDAY;
            case "Quarta-feira" -> DayOfWeek.WEDNESDAY;
            case "Quinta-feira" -> DayOfWeek.THURSDAY;
            case "Sexta-feira" -> DayOfWeek.FRIDAY;
            case "Sábado" -> DayOfWeek.SATURDAY;
            case "Domingo" -> DayOfWeek.SUNDAY;
            default -> null;
        };
    }

    private Map<Integer, Integer> getTotalGolsPorRodada() {
        return null;
    }

    private Map<Time, Integer> getTotalDeGolsPorTime() {
        return null;
    }

    private Map<Integer, Double> getMediaDeGolsPorRodada() {
        return null;
    }


}