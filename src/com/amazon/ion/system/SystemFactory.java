/*
 * Copyright (c) 2007-2008 Amazon.com, Inc.  All rights reserved.
 */

package com.amazon.ion.system;

import com.amazon.ion.IonCatalog;
import com.amazon.ion.IonSystem;
import com.amazon.ion.impl.IonSystemImpl;

/**
 * The bootstrap factory to create an application's {@link IonSystem}.
 * See the documentation of that class for important constraints.
 */
public final class SystemFactory
{
    /*
     * Potential configuration points:
     *
     * - default system version; could be lower than the latest supported.
     */

    /**
     * Constructs a new system instance with a default configuration.
     * <p>
     * The catalog used by the new instance will be a {@link SimpleCatalog}
     * with no initial entries.
     *
     * @return a new {@link IonSystem} instance; not null.
     */
    public static IonSystem newSystem()
    {
        return new IonSystemImpl();
    }


    /**
     * Constructs a new system instance with the given catalog.
     *
     * @return a new {@link IonSystem} instance; not null.
     *
     * @throws NullPointerException if {@code catalog} is null.
     */
    public static IonSystem newSystem(IonCatalog catalog)
    {
        if (catalog == null) throw new NullPointerException("catalog is null");

        return new IonSystemImpl(catalog);
    }
}
