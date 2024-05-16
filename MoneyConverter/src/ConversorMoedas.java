import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.Scanner;
import java.util.concurrent.CompletableFuture;

public class ConversorMoedas {

    private static final String CHAVE_API = "b86236aee1a93e371769aa06"; // Chave API
    private static final String URL_API = "https://v6.exchangerate-api.com/v6/" + CHAVE_API + "/latest/USD";
    private static final Gson gson = new Gson();

    public static void main(String[] args) {
        HttpClient cliente = HttpClient.newHttpClient();
        Scanner scanner = new Scanner(System.in);

        System.out.println("Bem-vindo ao Conversor de Moedas!");
        System.out.println("1. USD para EUR");
        System.out.println("2. USD para GBP");
        System.out.println("3. USD para JPY");
        System.out.println("4. USD para AUD");
        System.out.println("5. USD para CAD");
        System.out.println("6. USD para BRL");
        System.out.print("Escolha uma opção: ");

        int escolha = scanner.nextInt();
        String moedaDestino = obterMoedaDestino(escolha);

        if (moedaDestino != null) {
            try {
                HttpRequest requisicao = HttpRequest.newBuilder()
                        .uri(URI.create(URL_API))
                        .build();

                CompletableFuture<HttpResponse<String>> respostaFutura = cliente.sendAsync(requisicao, HttpResponse.BodyHandlers.ofString());

                respostaFutura.thenApply(HttpResponse::body)
                        .thenAccept(respostaJson -> {
                            double taxaDeCambio = parseTaxaDeCambio(respostaJson, moedaDestino);
                            System.out.print("Insira o valor em USD: ");
                            double valor = scanner.nextDouble();
                            double valorConvertido = valor * taxaDeCambio;
                            System.out.println("Valor convertido: " + valorConvertido + " " + moedaDestino);
                        }).join();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Opção inválida!");
        }

        scanner.close();
    }

    private static String obterMoedaDestino(int escolha) {
        return switch (escolha) {
            case 1 -> "EUR";
            case 2 -> "GBP";
            case 3 -> "JPY";
            case 4 -> "AUD";
            case 5 -> "CAD";
            case 6 -> "BRL";
            default -> null;
        };
    }

    private static double parseTaxaDeCambio(String respostaJson, String moedaDestino) {
        JsonObject objetoJson = gson.fromJson(respostaJson, JsonObject.class);
        JsonObject taxasDeCambio = objetoJson.getAsJsonObject("conversion_rates");
        return taxasDeCambio.get(moedaDestino).getAsDouble();
    }
}
