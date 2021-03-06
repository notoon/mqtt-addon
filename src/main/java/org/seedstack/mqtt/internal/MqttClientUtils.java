/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
/**
 * 
 */
package org.seedstack.mqtt.internal;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;
import org.seedstack.seed.SeedException;

/**
 * Methods to connect a {@link MqttClient} and to subscribe to different topics.
 * 
 * @author thierry.bouvet@mpsa.com
 *
 */
class MqttClientUtils {

    /**
     * Connect a new {@link MqttClient} to a broker.
     * 
     * @param mqttClient
     *            {@link MqttClient} to connect to the broker.
     * @param clientDefinition
     *            {@link MqttClientDefinition} to use to connect to the broker.
     * @throws MqttSecurityException
     *             if client can not connect to the broker.
     * @throws MqttException
     *             if client can not connect to the broker.
     */
    public static void connect(IMqttClient mqttClient, MqttClientDefinition clientDefinition)
            throws MqttSecurityException, MqttException {
        MqttConnectOptions options = clientDefinition.getConnectOptionsDefinition().getMqttConnectOptions();
        if (options != null) {
            mqttClient.connect(options);
        } else {
            mqttClient.connect();
        }
    }

    /**
     * Subscribe a {@link MqttClient} to topics.
     * 
     * @param mqttClient
     *            {@link MqttClient} used for subscription.
     * @param listenerDefinition
     *            {@link MqttListenerDefinition} which contains all topics
     *            definition
     * @throws MqttException
     *             if client can not connect to the broker.
     */
    public static void subscribe(IMqttClient mqttClient, MqttListenerDefinition listenerDefinition)
            throws MqttException {
        String[] topicFiler = listenerDefinition.getTopicFilter();
        int[] qos = listenerDefinition.getQos();
        mqttClient.subscribe(topicFiler, qos);
        // Fix PAHO bug: Test all qos values to throw an exception if one subscribe is not correct.
        // the qos tab is updated if a subscribe is failed but test is only on the first item of the list.
        for (int j = 0; j < qos.length; j++) {
            if (qos[j] == 0x80) {
                throw SeedException.wrap(new MqttException(MqttException.REASON_CODE_SUBSCRIBE_FAILED), MqttErrorCodes.SUBSCRIBE_FAILED)
                .put("topic", topicFiler[j]);
            }
        }

    }
}
