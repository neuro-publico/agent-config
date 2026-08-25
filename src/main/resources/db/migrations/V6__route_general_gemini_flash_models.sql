UPDATE agent_configs
SET model_ai = CASE
    WHEN LOWER(model_ai) LIKE 'gemini-%flash%lite%'
        THEN 'gemini-3.5-flash-lite'
    ELSE 'gemini-3.7-flash'
END,
updated_at = CURRENT_TIMESTAMP
WHERE model_ai = 'gemini'
   OR model_ai = 'gemini-flash-latest'
   OR (
       LOWER(model_ai) LIKE 'gemini-%flash%'
       AND LOWER(model_ai) NOT LIKE '%embedding%'
       AND LOWER(model_ai) NOT LIKE '%imagen%'
       AND LOWER(model_ai) NOT LIKE '%image%'
       AND LOWER(model_ai) NOT LIKE '%live%'
       AND LOWER(model_ai) NOT LIKE '%lyria%'
       AND LOWER(model_ai) NOT LIKE '%native-audio%'
       AND LOWER(model_ai) NOT LIKE '%omni%'
       AND LOWER(model_ai) NOT LIKE '%pro%'
       AND LOWER(model_ai) NOT LIKE '%tts%'
       AND LOWER(model_ai) NOT LIKE '%veo%'
   );
