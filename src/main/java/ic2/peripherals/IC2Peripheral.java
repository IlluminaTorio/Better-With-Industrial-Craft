

package ic2.peripherals;

import org.jetbrains.annotations.NotNull;
















public interface IC2Peripheral {
	@NotNull
	String getType();

	@NotNull
	String[] getMethodNames();

	@NotNull
	Object[] callMethod(@NotNull String method, @NotNull Object[] arguments) throws Exception;

	default boolean equals(IC2Peripheral other) {
		return this == other;
	}

	default String getHelp(@NotNull String method) {
		return null;
	}
}
