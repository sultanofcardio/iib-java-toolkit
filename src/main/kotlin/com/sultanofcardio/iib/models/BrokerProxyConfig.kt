package com.sultanofcardio.iib.models

import com.ibm.broker.config.proxy.BrokerProxy
import com.ibm.broker.config.proxy.ConfigManagerProxyLoggedException
import com.ibm.broker.config.proxy.ConfigManagerProxyPropertyNotInitializedException
import com.sultanofcardio.iib.interfaces.Configuration

@Suppress("MemberVisibilityCanBePrivate")
class BrokerProxyConfig(val configurableServiceName: String) : Configuration {

    override fun getStringProperty(property: String, defaultValue: String): String {
        val result = getStringProperty(property)
        return result ?: defaultValue
    }

    override fun getStringProperty(property: String): String? {
        val proxy = proxy
        var value: String? = null
        try {
            val service =
                proxy!!.getConfigurableService("UserDefined", configurableServiceName)
            if (service != null) value = service.properties.getProperty(property)
        } catch (e: ConfigManagerProxyPropertyNotInitializedException) {
            e.printStackTrace()
        }
        return value
    }

    override fun setProperty(property: String, value: String) {
        val proxy = proxy
        try {
            val service =
                proxy!!.getConfigurableService("UserDefined", configurableServiceName)
            service?.setProperty(property, value)
        } catch (e: ConfigManagerProxyLoggedException) {
            e.printStackTrace()
        } catch (e: ConfigManagerProxyPropertyNotInitializedException) {
            e.printStackTrace()
        }
    }

    @Throws(NumberFormatException::class)
    override fun getIntProperty(property: String): Int {
        val prop = getStringProperty(property)
        return prop?.toInt() ?: throw NumberFormatException("Cannot find property $property")
    }

    override fun getIntProperty(property: String, defaultValue: Int): Int {
        return try {
            getIntProperty(property)
        } catch (e: NumberFormatException) {
            defaultValue
        }
    }

    override fun getLongProperty(property: String): Long {
        val prop = getStringProperty(property)
        return prop?.toLong() ?: throw NumberFormatException("Cannot find property $property")
    }

    override fun getLongProperty(property: String, defaultValue: Long): Long {
        return try {
            getLongProperty(property)
        } catch (e: NumberFormatException) {
            defaultValue
        }
    }

    override fun getDoubleProperty(property: String): Double {
        val prop = getStringProperty(property)
        return prop?.toDouble() ?: throw NumberFormatException("Cannot find property $property")
    }

    override fun getDoubleProperty(property: String, defaultValue: Double): Double {
        return try {
            getDoubleProperty(property)
        } catch (e: NumberFormatException) {
            defaultValue
        }
    }

    companion object {
        private var proxy: BrokerProxy? = null
            get() {
                try {
                    if (field == null) field = BrokerProxy.getLocalInstance()
                    while (!field!!.hasBeenPopulatedByBroker()) {
                        Thread.sleep(500)
                    }
                } catch (e: Throwable) {
                    throw IllegalStateException("Unable to initialize BrokerProxy", e)
                }
                return field
            }
    }
}