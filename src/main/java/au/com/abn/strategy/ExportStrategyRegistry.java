package au.com.abn.strategy;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
@Log4j2
public class ExportStrategyRegistry {

    private Map<String, ExportStrategy> exportStrategies;

    public ExportStrategyRegistry(Set<ExportStrategy> exportStrategySet) {
        getExportStrategies(exportStrategySet);
    }

    public ExportStrategy getExportStrategy(final String outputType)
            throws ClassNotFoundException {
        final ExportStrategy exportStrategy = exportStrategies.get(outputType);
        if (null != exportStrategy) {
            return exportStrategy;
        }
        throw new ClassNotFoundException("Export Strategy not found for %s".formatted(outputType));
    }

    private void getExportStrategies(Set<ExportStrategy> exportStrategySet) {
        exportStrategies = new HashMap<>();
        exportStrategySet.forEach(exportStrategy -> exportStrategies
                .put(exportStrategy.exportTo(), exportStrategy));
    }

}
